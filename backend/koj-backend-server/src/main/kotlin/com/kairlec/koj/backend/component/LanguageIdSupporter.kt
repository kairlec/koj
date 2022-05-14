package com.kairlec.koj.backend.component

import com.kairlec.koj.common.taskTopic
import com.kairlec.koj.common.taskTopicPrefix
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.future.await
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.mono
import mu.KotlinLogging
import org.apache.pulsar.client.admin.PulsarAdmin
import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.ReactiveRedisOperations
import org.springframework.data.redis.core.deleteAndAwait
import org.springframework.data.redis.core.rangeAsFlow
import org.springframework.scheduling.annotation.Scheduled
import pro.chenggang.project.reactive.lock.core.ReactiveLockRegistry

class LanguageIdSupporter(
    private val pulsarAdmin: PulsarAdmin,
    private val redisReactiveLockRegistry: ReactiveLockRegistry,
    private val redisOperations: ReactiveRedisOperations<String, String>,
) : DisposableBean, InitializingBean {
    @Value("\${koj.namespace:public/default}")
    private lateinit var namespace: String

    suspend fun getSupportLanguageIds(): Flow<String> {
        return pulsarAdmin.topics().getListAsync(namespace).await().mapNotNull {
            "(?:non-)?persistent://${namespace}/$taskTopicPrefix(.*)".toRegex()
                .matchEntire(it)?.groupValues?.get(1)
        }.asFlow().filter { languageId ->
            pulsarAdmin.topics().getStatsAsync(taskTopic(languageId))
                .await()
                .subscriptions
                .filterKeys { it == "koj-sandbox" }
                .values
                .sumOf { it.consumers.size } > 0
        }
    }

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private val listOperations = redisOperations.opsForList()

    private val _supportLanguageChanges = MutableSharedFlow<List<String>>(3)
    val supportLanguageChanges = _supportLanguageChanges.asSharedFlow()

    @Scheduled(fixedDelay = 30_000)
    fun updateLanguageIds() {
        log.debug { "update language ids" }
        redisReactiveLockRegistry.obtain(UPDATE_LOCK_KEY).tryLockThenExecute {
            if (it) {
                log.debug { "get lock success, execute update" }
                runBlocking {
                    val supportLanguages = getSupportLanguageIds()
                    listOperations.deleteAndAwait(LANGUAGE_IDS_KEY)
                    supportLanguages.map { languageId ->
                        async { listOperations.leftPush(LANGUAGE_IDS_KEY, languageId).awaitSingle() }
                    }.toList().awaitAll()
                    log.debug { "emit language id list" }
                    _supportLanguageChanges.emit(supportLanguages.toList())
                }
            } else {
                log.debug { "get lock failed, execute pull" }
                coroutineScope.launch {
                    _supportLanguageChanges.emit(listOperations.rangeAsFlow(LANGUAGE_IDS_KEY, 0, -1).toList())
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

    override fun afterPropertiesSet() {
        updateLanguageIds()
    }
}

