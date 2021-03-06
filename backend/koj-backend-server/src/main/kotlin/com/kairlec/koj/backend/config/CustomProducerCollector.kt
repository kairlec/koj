package com.kairlec.koj.backend.config

import com.kairlec.koj.backend.component.LanguageIdSupporter
import com.kairlec.koj.backend.config.CustomProducerCollector.BuildProducerFunction
import com.kairlec.koj.common.taskTopic
import io.github.majusko.pulsar.collector.ProducerHolder
import io.github.majusko.pulsar.constant.Serialization
import io.github.majusko.pulsar.producer.ProducerCollector
import io.github.majusko.pulsar.properties.PulsarProperties
import io.github.majusko.pulsar.utils.UrlBuildService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import mu.KotlinLogging
import org.apache.pulsar.client.api.Producer
import org.apache.pulsar.client.api.PulsarClient
import org.apache.pulsar.client.api.interceptor.ProducerInterceptor
import kotlin.reflect.KProperty1
import kotlin.reflect.full.functions
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

class CustomProducerCollector(
    pulsarClient: PulsarClient,
    urlBuildService: UrlBuildService,
    pulsarProperties: PulsarProperties,
    producerInterceptor: ProducerInterceptor,
    languageIdSupporter: LanguageIdSupporter,
) : ProducerCollector(pulsarClient, urlBuildService, pulsarProperties, producerInterceptor) {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    @Suppress("UNCHECKED_CAST")
    private val superProducers by lazy {
        val property =
            ProducerCollector::class.memberProperties.first { it.name == "producers" } as KProperty1<CustomProducerCollector, MutableMap<String, Producer<*>>>
        property.apply { isAccessible = true }.get(this)
    }

    fun interface BuildProducerFunction {
        operator fun invoke(producerHolder: ProducerHolder): Producer<*>
    }

    private val buildProducerFunction: BuildProducerFunction by lazy {
        val function = ProducerCollector::class.functions.first { it.name == "buildProducer" }.apply {
            isAccessible = true
        }
        BuildProducerFunction {
            function.call(this, it) as Producer<*>
        }
    }

    fun addProducer(topic: String, clazz: Class<*>, serialization: Serialization) {
        if (superProducers.containsKey(topic)) {
            throw IllegalArgumentException("Producer for topic $topic already exists")
        }
        log.debug { "add new producer for topic:${topic}" }
        superProducers[topic] = buildProducerFunction(ProducerHolder(topic, clazz, serialization))
    }

    init {
        coroutineScope.launch {
            languageIdSupporter.supportLanguageChanges.collect { languageIds ->
                // ????????????topic
                val topics = superProducers.keys.toMutableSet()
                // ???????????????topic
                val jobs = languageIds.map { taskTopic(it) }.filter { topic ->
                    !topics.contains(topic).also {
                        if (it) {
                            topics.remove(topic)
                        }
                    }
                }.map { topic ->
                    launch {
                        log.info { "Create new producer for topic $topic" }
                        addProducer(topic, ByteArray::class.java, Serialization.BYTE)
                    }
                }.toList()
                // ?????????topic
                topics.forEach { topic ->
                    try {
                        log.info { "close producer for topic:${topic} because no subscriptions on it" }
                        superProducers.remove(topic)?.close()
                    } catch (e: Exception) {
                        log.error(e) { "close producer for topic:${topic} failed" }
                    }
                }
                jobs.joinAll()
            }
        }
    }

    companion object {
        private val log = KotlinLogging.logger { }
    }
}
