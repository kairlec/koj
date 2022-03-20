package com.kairlec.koj.backend

import com.kairlec.koj.backend.component.LanguageIdSupporter
import com.kairlec.koj.common.*
import com.kairlec.koj.model.Task
import com.kairlec.koj.model.TaskResult
import com.kairlec.koj.model.TaskStatus
import io.github.majusko.pulsar.producer.ProducerFactory
import io.github.majusko.pulsar.producer.PulsarTemplate
import io.github.majusko.pulsar.reactor.FluxConsumerFactory
import io.github.majusko.pulsar.reactor.PulsarFluxConsumer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.collect
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.apache.pulsar.client.api.PulsarClientException
import org.apache.pulsar.client.api.SubscriptionType
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RestController
import java.net.InetAddress
import kotlin.random.Random

@Configuration
class ProducerConfig(
    private val languageIdSupporter: LanguageIdSupporter,
) {
    @Bean
    fun producerFactory(): ProducerFactory {
        return ProducerFactory().apply {
            runBlocking {
                languageIdSupporter.getSupportLanguageIds().collect {
                    log.debug { "add new producer for topic:${taskTopic(it)}" }
                    addProducer(taskTopic(it), ByteArray::class.java)
                }
            }
        }
    }

    companion object {
        private val log = KotlinLogging.logger {}
    }
}

@Service
class SandboxMQ(
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
            .setSimple(false)
            .build()
    )

    private val resultConsumer = fluxConsumerFactory.newConsumer<ByteArray>(
        PulsarFluxConsumer.builder()
            .setTopic(resultTopic())
            .setConsumerName("koj-backend-result-${hostname}-${Random.nextLong()}")
            .setSubscriptionName("koj-backend")
            .setSubscriptionType(SubscriptionType.Shared)
            .setMessageClass(ByteArray::class.java)
            .setSimple(false)
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
    }

    private suspend fun taskStatus(data: ByteArray) {
        val status = TaskStatus.parseFrom(data)
        log.info { "task staus(${status.status}):${status}" }
    }

    private suspend fun taskResult(data: ByteArray) {
        val result = TaskResult.parseFrom(data)
        log.info { "task result(${result.type}):${result}" }
    }

    fun sendTask(task: Task) {
        producer.send(taskTopic(task.languageId), task.toByteArray().compress())
    }

}

@RestController
class Cont(
    private val mq: SandboxMQ
) {
//    @GetMapping
//    fun get(): String {
//        mq.sendTask(task {
//            id = 123456L
//            namespace = "kairlec"
//            code = """
//                fun main(){
//                    println(readln())
//                }
//            """.trimIndent()
//            languageId = Kotlin1610_Java11.id
//            stdin = "hello koj backend\n"
//            config = taskConfig {
//                maxTime = -1
//                maxMemory = -1
//                maxOutputSize = -1
//                maxStack = -1
//                maxProcessNumber = -1
//            }
//            debug = false
//        }.apply {
//            log.debug { "send task content:${this}" }
//        })
//        log.info { "sent task success" }
//        return "ok"
//    }

    companion object {
        private val log = KotlinLogging.logger { }
    }
}