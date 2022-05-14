package com.kairlec.koj.backend.config

import com.kairlec.koj.backend.component.LanguageIdSupporter
import com.kairlec.koj.backend.service.JudgeService
import com.kairlec.koj.backend.service.ProblemService
import com.kairlec.koj.backend.service.SubmitService
import com.kairlec.koj.common.*
import com.kairlec.koj.dao.model.SubmitState
import com.kairlec.koj.model.*
import io.github.majusko.pulsar.producer.ProducerCollector
import io.github.majusko.pulsar.producer.ProducerFactory
import io.github.majusko.pulsar.producer.PulsarTemplate
import io.github.majusko.pulsar.properties.PulsarProperties
import io.github.majusko.pulsar.reactor.FluxConsumerFactory
import io.github.majusko.pulsar.reactor.PulsarFluxConsumer
import io.github.majusko.pulsar.utils.UrlBuildService
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.reactive.collect
import mu.KotlinLogging
import org.apache.pulsar.client.admin.PulsarAdmin
import org.apache.pulsar.client.api.PulsarClient
import org.apache.pulsar.client.api.SubscriptionType
import org.apache.pulsar.client.api.interceptor.ProducerInterceptor
import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.getBean
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.EventListener
import org.springframework.data.redis.core.ReactiveRedisOperations
import pro.chenggang.project.reactive.lock.core.ReactiveLockRegistry
import java.net.InetAddress
import kotlin.random.Random

@Configuration
class SandboxConfig {
    @Bean
    fun customProducer(
        pulsarClient: PulsarClient,
        urlBuildService: UrlBuildService,
        pulsarProperties: PulsarProperties,
        producerInterceptor: ProducerInterceptor,
        languageIdSupporter: LanguageIdSupporter,
    ): ProducerCollector {
        return CustomProducerCollector(
            pulsarClient,
            urlBuildService,
            pulsarProperties,
            producerInterceptor,
            languageIdSupporter,
        )
    }

    @Bean
    fun producerFactory(languageIdSupporter: LanguageIdSupporter): ProducerFactory {
        return ProducerFactory().apply {
            runBlocking {
                languageIdSupporter.getSupportLanguageIds().collect {
                    log.debug { "add new producer for topic:${taskTopic(it)}" }
                    addProducer(taskTopic(it), ByteArray::class.java)
                }
            }
        }
    }

    @Bean
    fun sandboxMQ(
        fluxConsumerFactory: FluxConsumerFactory,
        producer: PulsarTemplate<ByteArray>,
        applicationContext: ApplicationContext,
        problemService: ProblemService,
        judgeService: JudgeService
    ): SandboxMQ {
        return SandboxMQ(fluxConsumerFactory, producer, applicationContext, problemService, judgeService)
    }

    @Bean
    fun languageIdSupporter(
        pulsarAdmin: PulsarAdmin,
        redisReactiveLockRegistry: ReactiveLockRegistry,
        redisOperations: ReactiveRedisOperations<String, String>,
    ): LanguageIdSupporter {
        return LanguageIdSupporter(
            pulsarAdmin,
            redisReactiveLockRegistry,
            redisOperations,
        )
    }

    companion object {
        private val log = KotlinLogging.logger {}
    }
}

class SandboxMQ(
    fluxConsumerFactory: FluxConsumerFactory,
    private val producer: PulsarTemplate<ByteArray>,
    private val applicationContext: ApplicationContext,
    private val problemService: ProblemService,
    private val judgeService: JudgeService
) : DisposableBean {
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

    private val dlqConsumer = fluxConsumerFactory.newConsumer<ByteArray>(
        PulsarFluxConsumer.builder()
            .setTopic(deadLetterTopic())
            .setConsumerName("koj-backend-dlq-${hostname}-${Random.nextLong()}")
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
                } catch (e: Throwable) {
                    msg.consumer.negativeAcknowledge(msg.message)
                    log.error(e) { "receive status error:${e.message}" }
                }
            }
        }
        coroutineScope.launch {
            resultConsumer.asFlux().collect { msg ->
                try {
                    val data = (msg.message.value as ByteArray).decompress()
                    taskResult(data)
                    msg.consumer.acknowledge(msg.message)
                } catch (e: Throwable) {
                    msg.consumer.negativeAcknowledge(msg.message)
                    log.error(e) { "receive result error:${e.message}" }
                }
            }
        }
        coroutineScope.launch {
            dlqConsumer.asFlux().collect { msg ->
                try {
                    val data = (msg.message.value as ByteArray).decompress()
                    taskDead(data)
                    msg.consumer.acknowledge(msg.message)
                } catch (e: Throwable) {
                    msg.consumer.negativeAcknowledge(msg.message)
                    log.error(e) { "receive dlq error:${e.message}" }
                }
            }
        }
    }

    private val submitService by lazy {
        applicationContext.getBean<SubmitService>()
    }

    @OptIn(InternalApi::class)
    private suspend fun taskStatus(data: ByteArray) {
        val status = TaskStatus.parseFrom(data)
        log.info { "task status(${status.status}):${status}" }
        submitService.updateSubmit(status.id, status.status.asSubmitState(), null, null)
    }

    @OptIn(InternalApi::class)
    private suspend fun taskDead(data: ByteArray) {
        val status = Task.parseFrom(data)
        log.info { "task status(failed of dlq):${status}" }
        submitService.updateSubmit(status.id, SubmitState.NO_SANDBOX, null, null)
    }

    @OptIn(InternalApi::class)
    private suspend fun taskResult(data: ByteArray) {
        val result = TaskResult.parseFrom(data)
        log.info { "task result(${result.type}):${result}" }
        submitService.updateSubmit(
            result.id,
            result.type.asSubmitState(),
            result.memory,
            result.time,
            result.stderr,
            result.stdout
        )
        if (result.type == TaskResultType.NO_ERROR) {
            judge(result.id, result.stdout)
        }
    }

    @OptIn(InternalApi::class)
    private suspend fun judge(submitId: Long, stdout: String) {
        coroutineScope.launch {
            try {
                val problemId = submitService.getProblemIdOfSubmit(submitId)
                if (problemId == null) {
                    log.error { "judge task of submit[id=${submitId}] error because cannot find problem id." }
                    submitService.updateSubmit(
                        submitId,
                        SubmitState.SYSTEM_ERROR
                    )
                    return@launch
                }
                val problemRun = problemService.getProblemAnsout(problemId)
                if (problemRun == null) {
                    log.error { "judge task of submit[id=${submitId}] error because cannot find problem ansout." }
                    submitService.updateSubmit(
                        submitId,
                        SubmitState.SYSTEM_ERROR
                    )
                    return@launch
                }
                val state = judgeService.normalJudge(stdout, problemRun)
                submitService.updateSubmit(
                    submitId,
                    state
                )
            } catch (e: Throwable) {
                if (e is CancellationException) {
                    log.error(e) { "judge task of submit[id=${submitId}] has been canceled" }
                } else {
                    log.error(e) { "judge task of submit[id=${submitId}] error" }
                }
                submitService.updateSubmit(
                    submitId,
                    SubmitState.SYSTEM_ERROR
                )
            }
        }
    }

    private fun TaskIntermediateStatusEnum.asSubmitState(): SubmitState {
        return when (this) {
            TaskIntermediateStatusEnum.COMPILING -> SubmitState.IN_COMPILING
            TaskIntermediateStatusEnum.RUNNING -> SubmitState.IN_RUNNING
            TaskIntermediateStatusEnum.UNRECOGNIZED -> SubmitState.UNKNOWN
        }
    }

    private fun TaskResultType.asSubmitState(): SubmitState {
        return when (this) {
            TaskResultType.NO_ERROR -> SubmitState.IN_JUDGING
            TaskResultType.CPU_TIME_LIMIT_EXCEEDED,
            TaskResultType.REAL_TIME_LIMIT_EXCEEDED -> SubmitState.TIME_LIMIT_EXCEEDED
            TaskResultType.MEMORY_LIMIT_EXCEEDED -> SubmitState.MEMORY_LIMIT_EXCEEDED
            TaskResultType.RUNTIME_ERROR -> SubmitState.RUNTIME_ERROR
            TaskResultType.SYSTEM_ERROR -> SubmitState.SYSTEM_ERROR
            TaskResultType.OUTPUT_LIMIT_EXCEEDED -> SubmitState.OUTPUT_LIMIT_EXCEEDED
            TaskResultType.COMPILE_ERROR -> SubmitState.COMPILATION_ERROR
            TaskResultType.UNRECOGNIZED -> SubmitState.UNKNOWN
        }
    }

    fun sendTask(task: Task) {
        producer.send(taskTopic(task.languageId), task.toByteArray().compress())
    }

    override fun destroy() {
        coroutineScope.cancel()
    }

}
