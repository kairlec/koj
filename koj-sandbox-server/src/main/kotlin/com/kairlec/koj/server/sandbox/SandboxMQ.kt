package com.kairlec.koj.server.sandbox

import com.kairlec.koj.common.*
import com.kairlec.koj.core.*
import com.kairlec.koj.model.*
import com.kairlec.koj.sandbox.docker.Docker
import com.kairlec.koj.sandbox.docker.DockerSandboxInitConfig
import io.github.majusko.pulsar.producer.ProducerFactory
import io.github.majusko.pulsar.producer.PulsarTemplate
import io.github.majusko.pulsar.reactor.FluxConsumer
import io.github.majusko.pulsar.reactor.FluxConsumerFactory
import io.github.majusko.pulsar.reactor.PulsarFluxConsumer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.apache.pulsar.client.api.PulsarClientException
import org.apache.pulsar.client.api.SubscriptionType
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import java.net.InetAddress
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import kotlin.random.Random

@Configuration
class ProducerConfig {
    @Bean
    fun producerFactory(): ProducerFactory {
        return ProducerFactory()
            .addProducer(statusTopic(), ByteArray::class.java)
            .addProducer(resultTopic(), ByteArray::class.java)
    }
}

@Service
internal class SandboxMQ(
    private val fluxConsumerFactory: FluxConsumerFactory,
    private val producer: PulsarTemplate<ByteArray>
) {
    companion object {
        private val log = KotlinLogging.logger { }
        private val hostname = InetAddress.getLocalHost().hostName
    }

    private val dispatcher = ThreadPoolExecutor(
        1,
        Runtime.getRuntime().availableProcessors() + 3,
        1,
        TimeUnit.MINUTES,
        LinkedBlockingQueue(),
    ).asCoroutineDispatcher()

    private val coroutineScope = CoroutineScope(dispatcher)

    private fun newConsumer(language: Language): FluxConsumer<ByteArray> {
        return fluxConsumerFactory.newConsumer(
            PulsarFluxConsumer.builder()
                .setTopic(taskTopic(language.id))
                .setConsumerName("koj-sandbox-${language.id}-$hostname-${Random.nextLong()}")
                .setSubscriptionName("koj-sandbox")
                .setSubscriptionType(SubscriptionType.Shared)
                .setMessageClass(ByteArray::class.java)
                .setSimple(false)
                .build()
        )
    }

    @EventListener(ApplicationReadyEvent::class)
    fun subscribe() {
        runBlocking {
            Docker.init(DockerSandboxInitConfig(emptyList()))
        }
        val taskFlows = KojFactory.supportLanguages.map {
            newConsumer(it).asFlux().asFlow()
        }
        taskFlows.forEach {
            coroutineScope.launch {
                it.collect { msg ->
                    try {
                        val data = (msg.message.value as ByteArray).decompress()
                        receive(data)
                        msg.consumer.acknowledge(msg.message)
                    } catch (e: PulsarClientException) {
                        msg.consumer.negativeAcknowledge(msg.message)
                    } catch (e: Exception) {
                        throw e
                    }
                }
            }
        }
    }

    private suspend fun receive(data: ByteArray) {
        val task = Task.parseFrom(data)
        val context = KojFactory.create(
            id = task.id,
            namespace = task.namespace,
            tempDirectory = TempDirectory.createOrUse(task.id.toString()),
            useLanguage = KojFactory.supportLanguages.first { it.id == task.languageId },
            code = task.code,
            stdin = task.stdin,
            runConfig = RunConfig(
                maxTime = task.config.maxTime,
                maxMemory = task.config.maxMemory,
                maxOutputSize = task.config.maxOutputSize,
                maxStack = task.config.maxStack,
                maxProcessNumber = task.config.maxProcessNumber,
                args = task.config.argsList,
                env = task.config.envList
            ),
            debug = task.debug
        )
        coroutineScope.launch {
            context.state.collect {
                when (it.current) {
                    State.INITED -> {
                        producer.send(statusTopic(), taskStatus {
                            id = task.id
                            status = TaskIntermediateStatusEnum.COMPILING
                        }.toByteArray().compress())
                    }
                    State.COMPILED -> {
                        producer.send(statusTopic(), taskStatus {
                            id = task.id
                            status = TaskIntermediateStatusEnum.RUNNING
                        }.toByteArray().compress())
                    }
                    State.END -> {
                        if (it.isError) {
                            log.info(it.cause) { "task error: ${it.stdout}\n\n${it.stderr}" }
                            producer.send(resultTopic(), taskResult {
                                id = task.id
                                type = when (it.cause) {
                                    is CompilerException -> {
                                        TaskResultType.COMPILE_ERROR
                                    }
                                    is ExecuteResultException -> {
                                        (it.cause as ExecuteResultException).result.asTaskResultType()
                                    }
                                    else -> {
                                        log.error(it.cause) { "Unknown error" }
                                        TaskResultType.SYSTEM_ERROR
                                    }
                                }
                                stdout = it.stdout ?: ""
                                stderr = it.stderr ?: ""
                            }.toByteArray().compress())
                        } else {
                            log.info { "task end:${it}" }
                            producer.send(resultTopic(), taskResult {
                                id = task.id
                                stdout = it.stdout ?: ""
                                stderr = it.stderr ?: ""
                                time = it.time
                                memory = it.memory
                                type = TaskResultType.NO_ERROR
                            }.toByteArray().compress())
                        }
                    }
                }
            }
        }
        context.run()
    }

}

private fun ExecuteResultType.asTaskResultType(): TaskResultType {
    return when (this) {
        ExecuteResultType.AC -> TaskResultType.NO_ERROR
        ExecuteResultType.TLE -> TaskResultType.CPU_TIME_LIMIT_EXCEEDED
        ExecuteResultType.MLE -> TaskResultType.MEMORY_LIMIT_EXCEEDED
        ExecuteResultType.RE -> TaskResultType.RUNTIME_ERROR
        ExecuteResultType.OLE -> TaskResultType.OUTPUT_LIMIT_EXCEEDED
        ExecuteResultType.CE -> TaskResultType.COMPILE_ERROR
        // 下面这2个是不会出现的,因为判题不在sandbox
        ExecuteResultType.WA,
        ExecuteResultType.PE,
        ExecuteResultType.SE -> TaskResultType.SYSTEM_ERROR
    }
}