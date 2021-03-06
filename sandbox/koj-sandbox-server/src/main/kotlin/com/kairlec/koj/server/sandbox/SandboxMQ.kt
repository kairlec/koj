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
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.reactive.asFlow
import mu.KotlinLogging
import org.apache.pulsar.client.api.SubscriptionType
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationContext
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
    private val producer: PulsarTemplate<ByteArray>,
    applicationContext: ApplicationContext,
) {
    private val dispatcher = ThreadPoolExecutor(
        1,
        Runtime.getRuntime().availableProcessors() + 3,
        1,
        TimeUnit.MINUTES,
        LinkedBlockingQueue(),
    ).asCoroutineDispatcher()

    private val coroutineScope = CoroutineScope(dispatcher)

    private val applicationName = applicationContext.applicationName.ifBlank {
        "koj-sb-app-server"
    }

    private val consumerNameSuffix = "$hostname-${Random.nextLong(0, Long.MAX_VALUE)}-${applicationName}"

    private fun newConsumer(language: Language): FluxConsumer<ByteArray> {
        return fluxConsumerFactory.newConsumer(
            PulsarFluxConsumer.builder()
                .setTopic(taskTopic(language.id))
                .setConsumerName("koj-sandbox-${language.id}-${consumerNameSuffix}")
                .setSubscriptionName("koj-sandbox")
                .setSubscriptionType(SubscriptionType.Shared)
                .setMessageClass(ByteArray::class.java)
                .setSimple(false)
                .setDeadLetterTopic(deadLetterTopic())
                .setMaxRedeliverCount(5)
                .build()
        )
    }

    @EventListener(ApplicationReadyEvent::class)
    fun subscribe() {
        runBlocking {
            Docker.init(
                DockerSandboxInitConfig(
                    listOf(
                        "kairlec/koj-support:py36",
                        "kairlec/koj-support:py38",
                        "kairlec/koj-support:py310",
                        "kairlec/koj-support:clike",
                        "kairlec/koj-support:jvm8",
                        "kairlec/koj-support:jvm11",
                        "kairlec/koj-support:jvm17",
                    )
                )
            )
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
                    } catch (e: Throwable) {
                        msg.consumer.negativeAcknowledge(msg.message)
                        log.error(e) { "receive task error:${e.message}" }
                    }
                }
            }
        }
    }

    private suspend fun receive(data: ByteArray) {
        receive(Task.parseFrom(data))
    }

    internal suspend fun receive(task: Task) {
        log.debug { "receiver task :\n${task}" }
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
                log.info { "(id=${task.id})state -> $it" }
                when (it.current) {
                    State.INITED -> {
                        log.info { "(id=${task.id}) send status -> COMPILING" }
                        producer.send(statusTopic(), taskStatus {
                            id = task.id
                            status = TaskIntermediateStatusEnum.COMPILING
                            processorName = consumerNameSuffix
                        }.toByteArray().compress())
                    }
                    State.COMPILED -> {
                        log.info { "(id=${task.id}) send status -> RUNNING" }
                        producer.send(statusTopic(), taskStatus {
                            id = task.id
                            status = TaskIntermediateStatusEnum.RUNNING
                            processorName = consumerNameSuffix
                        }.toByteArray().compress())
                    }
                    State.END -> {
                        if (it.isError) {
                            log.info(it.cause) { "(id=${task.id})task error: ${it.stdout}\n\n${it.stderr}" }
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
                                processorName = consumerNameSuffix
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
                                processorName = consumerNameSuffix
                            }.toByteArray().compress())
                        }
                    }
                }
            }
        }
        delay(50)
        context.run()
    }

    companion object {
        private val log = KotlinLogging.logger { }
        private val hostname = InetAddress.getLocalHost().hostName
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
        // ?????????2?????????????????????,??????????????????sandbox
        ExecuteResultType.WA,
        ExecuteResultType.PE,
        ExecuteResultType.SE -> TaskResultType.SYSTEM_ERROR
    }
}