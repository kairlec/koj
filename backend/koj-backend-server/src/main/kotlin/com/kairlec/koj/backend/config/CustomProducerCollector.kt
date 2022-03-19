package com.kairlec.koj.backend.config

import com.kairlec.koj.backend.component.LanguageIdSupporter
import com.kairlec.koj.backend.config.CustomProducerCollector.BuildProducerFunction
import com.kairlec.koj.common.taskTopic
import io.github.majusko.pulsar.collector.ProducerHolder
import io.github.majusko.pulsar.constant.Serialization
import io.github.majusko.pulsar.producer.ProducerCollector
import io.github.majusko.pulsar.utils.UrlBuildService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import mu.KotlinLogging
import org.apache.pulsar.client.api.Producer
import org.apache.pulsar.client.api.PulsarClient
import org.springframework.stereotype.Component
import kotlin.reflect.KProperty1
import kotlin.reflect.full.functions
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

@Component
class CustomProducerCollector(
    pulsarClient: PulsarClient,
    urlBuildService: UrlBuildService,
    languageIdSupporter: LanguageIdSupporter,
) : ProducerCollector(pulsarClient, urlBuildService) {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

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
        superProducers[topic] = buildProducerFunction(ProducerHolder(topic, clazz, serialization))
    }

    init {
        coroutineScope.launch {
            languageIdSupporter.supportLanguageChanges.collect { languageIds ->
                val topics = superProducers.keys.toMutableSet()
                languageIds.collect { languageId ->
                    val topic = taskTopic(languageId)
                    if (superProducers.containsKey(topic)) {
                        topics.remove(topic)
                    } else {
                        addProducer(topic, ByteArray::class.java, Serialization.BYTE)
                    }
                }
                topics.forEach { topic ->
                    try {
                        superProducers.remove(topic)?.close()
                    } catch (e: Exception) {
                        log.error(e) { "close producer for topic:${topic} failed" }
                    }
                }
            }
        }
    }

    companion object {
        private val log = KotlinLogging.logger { }
    }
}
