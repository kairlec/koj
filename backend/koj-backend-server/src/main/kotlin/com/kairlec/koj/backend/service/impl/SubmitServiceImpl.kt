package com.kairlec.koj.backend.service.impl

import com.baidu.fsg.uid.UidGenerator
import com.kairlec.koj.backend.component.LanguageIdSupporter
import com.kairlec.koj.backend.exp.PermissionDeniedException
import com.kairlec.koj.backend.service.SubmitService
import com.kairlec.koj.common.InternalApi
import com.kairlec.koj.dao.extended.ListCondition
import com.kairlec.koj.dao.model.SimpleSubmit
import com.kairlec.koj.dao.model.SubmitDetail
import com.kairlec.koj.dao.model.SubmitState
import com.kairlec.koj.dao.repository.CompetitionRepository
import com.kairlec.koj.dao.repository.PageData
import com.kairlec.koj.dao.repository.ProblemRepository
import com.kairlec.koj.dao.repository.SubmitRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service

@Service
class SubmitServiceImpl(
    private val submitRepository: SubmitRepository,
    private val competitionRepository: CompetitionRepository,
    private val uidGenerator: UidGenerator,
    private val problemRepository: ProblemRepository,
    private val languageIdSupporter: LanguageIdSupporter,
) : SubmitService {
    override suspend fun getSubmits(listCondition: ListCondition): PageData<SimpleSubmit> {
        return submitRepository.getSubmitRank(listCondition)
    }

    override suspend fun getSubmit(userId: Long, submitId: Long): SubmitDetail? {
        return submitRepository.getSubmitDetail(userId, submitId)
    }

    override suspend fun getSubmits(userId: Long, competitionId: Long): Flow<SimpleSubmit> {
        if (!competitionRepository.isInCompetition(userId, competitionId)) {
            throw PermissionDeniedException("not in this competition")
        }
        return withContext(Dispatchers.IO) {
            submitRepository.getSubmitRank(competitionId)
        }
    }

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

    override suspend fun createSubmit(
        userId: Long,
        competitionId: Long?,
        languageId: String,
        problemId: Long,
        code: String
    ): Long {
        if (languageIdSupporter.supportLanguageChanges.replayCache.lastOrNull()?.any { it == languageId } != true) {
            TODO("throw language not supported exception")
//            throw LanguageNotSupport("language not support")
        }
        TODO("获取题目内容和语言要求,创建沙盒任务并提交")
        return uidGenerator.uid.also {
            submitRepository.createSubmit(it, userId, competitionId, languageId, problemId, code)
        }
    }

    @InternalApi
    override suspend fun updateSubmit(id: Long, state: SubmitState, castMemory: Int?, castTime: Int?): Boolean {
        return submitRepository.updateSubmit(id, state, castMemory, castTime)
    }

}