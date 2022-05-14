package com.kairlec.koj.backend.service.impl

import com.baidu.fsg.uid.UidGenerator
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.kairlec.koj.backend.component.LanguageIdSupporter
import com.kairlec.koj.backend.config.SandboxMQ
import com.kairlec.koj.backend.exp.NotSupportLanguageConfigException
import com.kairlec.koj.backend.exp.NotSupportLanguageException
import com.kairlec.koj.backend.service.SubmitService
import com.kairlec.koj.common.InternalApi
import com.kairlec.koj.dao.model.SubmitState
import com.kairlec.koj.dao.repository.ProblemRepository
import com.kairlec.koj.dao.repository.SubmitRepository
import com.kairlec.koj.model.task
import com.kairlec.koj.model.taskConfig
import mu.KotlinLogging
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.net.InetAddress
import kotlin.random.Random

@Service
@ConditionalOnBean(SandboxMQ::class, LanguageIdSupporter::class)
class SubmitServiceImpl(
    private val submitRepository: SubmitRepository,
    private val uidGenerator: UidGenerator,
    private val problemRepository: ProblemRepository,
    private val languageIdSupporter: LanguageIdSupporter,
    private val sandboxMQ: SandboxMQ,
    private val objectMapper: ObjectMapper,
    applicationContext: ApplicationContext
) : SubmitService {
    @InternalApi
    override suspend fun createSubmit(
        id: Long,
        userId: Long,
        competitionId: Long?,
        languageId: String,
        problemId: Long,
        code: String
    ) {
        return submitRepository.createSubmit(id, userId, competitionId, languageId, problemId, code)
    }

    @Transactional
    @OptIn(InternalApi::class)
    override suspend fun createSubmit(
        userId: Long,
        competitionId: Long?,
        languageId: String,
        problemId: Long,
        code: String
    ): Long {
        if (languageIdSupporter.supportLanguageChanges.replayCache.lastOrNull()?.any { it == languageId } != true) {
            throw NotSupportLanguageException(languageId)
        }
        val problemConfig =
            problemRepository.getProblemConfig(problemId, languageId)
                ?: throw NotSupportLanguageConfigException(languageId)
        val problemStdin =
            problemRepository.getProblemStdin(problemId) ?: throw NotSupportLanguageConfigException(languageId)
        val id = uidGenerator.uid
        submitRepository.createSubmit(id, userId, competitionId, languageId, problemId, code)
        sandboxMQ.sendTask(task {
            this.id = id
            this.namespace = "${userId}_${problemId}"
            this.code = code
            this.languageId = languageId
            this.config = taskConfig {
                this.maxTime = problemConfig.time
                this.maxMemory = problemConfig.memory.toLong()
                this.maxOutputSize = problemConfig.maxOutputSize.toLong()
                this.maxStack = problemConfig.maxStack.toLong()
                this.maxProcessNumber = problemConfig.maxProcessNumber.toInt()
                this.args.addAll(objectMapper.readValue<List<String>>(problemConfig.args))
                this.env.addAll(objectMapper.readValue<List<String>>(problemConfig.env))
            }
            this.stdin = problemStdin
            this.debug = false
            this.processorName = consumerNameSuffix
        })
        return id
    }

    @InternalApi
    override suspend fun updateSubmit(
        id: Long,
        state: SubmitState,
        castMemory: Long?,
        castTime: Long?,
        stderr: String?,
        stdout: String?
    ): Boolean {
        return submitRepository.updateSubmit(id, state, castMemory, castTime, stderr, stdout)
    }

    @InternalApi
    override suspend fun getProblemIdOfSubmit(id: Long): Long? {
        return submitRepository.getProblemIdOfSubmit(id)
    }

    override fun getLanguages(): List<String> {
        val last = languageIdSupporter.supportLanguageChanges.replayCache.lastOrNull()
        if (last == null) {
            log.warn { "language id list never published. so just get empty list." }
            return emptyList()
        } else {
            return last
        }
    }

    private val applicationName = applicationContext.applicationName.ifBlank {
        "koj-be-server"
    }

    private val consumerNameSuffix = "$hostname-${Random.nextLong()}-${applicationName}"

    companion object {
        private val log = KotlinLogging.logger { }
        private val hostname = InetAddress.getLocalHost().hostName
    }
}