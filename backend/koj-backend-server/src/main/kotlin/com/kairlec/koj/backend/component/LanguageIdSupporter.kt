package com.kairlec.koj.backend.component

import com.kairlec.koj.common.taskTopic
import com.kairlec.koj.common.taskTopicPrefix
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.reactor.mono
import mu.KotlinLogging
import org.apache.pulsar.client.admin.PulsarAdmin
import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.ReactiveRedisOperations
import org.springframework.data.redis.core.deleteAndAwait
import org.springframework.data.redis.core.leftPushAllAndAwait
import org.springframework.data.redis.core.rangeAsFlow
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import pro.chenggang.project.reactive.lock.core.ReactiveLockRegistry

@Component
class LanguageIdSupporter(
    private val pulsarAdmin: PulsarAdmin,
    private val redisReactiveLockRegistry: ReactiveLockRegistry,
    private val redisOperations: ReactiveRedisOperations<String, String>,
) : DisposableBean {
    @Value("\${koj.namespace:public/default}")
    private lateinit var namespace: String

    fun getSupportLanguageIds(): List<String> {
        return pulsarAdmin.topics().getList(namespace).mapNotNull {
            "(?:persistent|non-persistent)://${namespace}/$taskTopicPrefix(.*)".toRegex()
                .matchEntire(it)?.groupValues?.get(1)
        }
    }

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private val listOperations = redisOperations.opsForList()

    private val _supportLanguageChanges = MutableSharedFlow<Flow<String>>(3)
    val supportLanguageChanges = _supportLanguageChanges.asSharedFlow()

    @Scheduled(fixedDelay = 10_000)
    fun updateLanguageIds() {
        log.debug { "update language ids" }
        redisReactiveLockRegistry.obtain(UPDATE_LOCK_KEY).tryLockThenExecute {
            if (it) {
                log.debug { "get lock success, execute update" }
                runBlocking {
                    val supportLanguages = getSupportLanguageIds()
                    listOperations.deleteAndAwait(LANGUAGE_IDS_KEY)
                    listOperations.leftPushAllAndAwait(LANGUAGE_IDS_KEY, supportLanguages)
                    _supportLanguageChanges.emit(supportLanguages.asFlow())
                }
            } else {
                log.debug { "get lock failed, execute pull" }
                coroutineScope.launch {
                    _supportLanguageChanges.emit(listOperations.rangeAsFlow(LANGUAGE_IDS_KEY, 0, -1))
                }
            }
            mono { }
        }.block()
    }

    companion object {
        private const val UPDATE_LOCK_KEY = "@@@LANGUAGE_UPDATE_LOCK_KEY@@@"
        private const val LANGUAGE_IDS_KEY = "@@@KOJ_LANGUAGE_IDS@@@"
        private val log = KotlinLogging.logger { }
    }

    override fun destroy() {
        if (coroutineScope.isActive) {
            log.debug { "close language supporter" }
            coroutineScope.cancel()
        }
    }
}

