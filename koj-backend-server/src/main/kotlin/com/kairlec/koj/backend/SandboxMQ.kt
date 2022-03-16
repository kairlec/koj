package com.kairlec.koj.backend

import com.kairlec.koj.common.*
import com.kairlec.koj.core.KojFactory
import com.kairlec.koj.language.kotlin.Kotlin1610_Java11
import com.kairlec.koj.model.*
import io.github.majusko.pulsar.producer.ProducerFactory
import io.github.majusko.pulsar.producer.PulsarTemplate
import io.github.majusko.pulsar.reactor.FluxConsumerFactory
import io.github.majusko.pulsar.reactor.PulsarFluxConsumer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.collect
import mu.KotlinLogging
import org.apache.pulsar.client.api.PulsarClientException
import org.apache.pulsar.client.api.SubscriptionType
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import java.net.InetAddress
import kotlin.random.Random

@Configuration
class ProducerConfig {
    @Bean
    fun producerFactory(): ProducerFactory {
        return ProducerFactory().apply {
            KojFactory.languages.forEach {
                addProducer(taskTopic(it.id), ByteArray::class.java)
            }
        }
    }
}

@Service
internal class SandboxMQ(
    fluxConsumerFactory: FluxConsumerFactory,
    private val producer: PulsarTemplate<ByteArray>
) {
    companion object {
        private val log = KotlinLogging.logger { }
        private val hostname = InetAddress.getLocalHost().hostName
    }

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private val statusConsumer = fluxConsumerFactory.newConsumer<ByteArray>(
        PulsarFluxConsumer.builder()
            .setTopic(statusTopic())
            .setConsumerName("koj-backend-status-${hostname}-${Random.nextLong()}")
            .setSubscriptionName("koj-backend")
            .setSubscriptionType(SubscriptionType.Shared)
            .setMessageClass(ByteArray::class.java)
            .build()
    )

    private val resultConsumer = fluxConsumerFactory.newConsumer<ByteArray>(
        PulsarFluxConsumer.builder()
            .setTopic(resultTopic())
            .setConsumerName("koj-backend-result-${hostname}-${Random.nextLong()}")
            .setSubscriptionName("koj-backend")
            .setSubscriptionType(SubscriptionType.Shared)
            .setMessageClass(ByteArray::class.java)
            .build()
    )

    @EventListener(ApplicationReadyEvent::class)
    fun subscribe() {
        coroutineScope.launch {
            statusConsumer.asFlux().collect { msg ->
                try {
                    val data = (msg.message.value as ByteArray).decompress()
                    taskStatus(data)
                    msg.consumer.acknowledge(msg.message)
                } catch (e: PulsarClientException) {
                    msg.consumer.negativeAcknowledge(msg.message)
                }
            }
        }
        coroutineScope.launch {
            resultConsumer.asFlux().collect { msg ->
                try {
                    val data = (msg.message.value as ByteArray).decompress()
                    taskResult(data)
                    msg.consumer.acknowledge(msg.message)
                } catch (e: PulsarClientException) {
                    msg.consumer.negativeAcknowledge(msg.message)
                }
            }
        }
        sendTask(task {
            id = 123456L
            namespace = "kairlec"
            code = """
                fun main(){
                    println(readln())
                }
            """.trimIndent()
            languageId = Kotlin1610_Java11.id
            stdin = "hello koj backend\n"
            config = taskConfig {
                maxTime = -1
                maxMemory = -1
                maxOutputSize = -1
                maxStack = -1
                maxProcessNumber = -1
            }
        })
    }

    private suspend fun taskStatus(data: ByteArray) {
        val status = TaskStatus.parseFrom(data)
        log.info { status }
    }

    private suspend fun taskResult(data: ByteArray) {
        val result = TaskResult.parseFrom(data)
        log.info { result }
    }

    fun sendTask(task: Task) {
        producer.send(taskTopic(task.languageId), task.toByteArray().compress())
    }

}
