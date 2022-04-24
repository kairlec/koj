package com.kairlec.koj.backend.service.impl

import com.kairlec.koj.backend.service.SubmitService
import com.kairlec.koj.common.InternalApi
import com.kairlec.koj.dao.extended.ListCondition
import com.kairlec.koj.dao.model.SimpleSubmit
import com.kairlec.koj.dao.model.SubmitDetail
import com.kairlec.koj.dao.model.SubmitState
import com.kairlec.koj.dao.repository.PageData
import com.kairlec.koj.dao.repository.SubmitRepository
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service

@Service
class SubmitServiceImpl(
    private val submitRepository: SubmitRepository
) : SubmitService {
    override suspend fun getSubmits(listCondition: ListCondition): PageData<SimpleSubmit> {
        return submitRepository.getSubmitRank(listCondition)
    }

    override suspend fun getSubmit(userId: Long, submitId: Long): SubmitDetail? {
        return submitRepository.getSubmitDetail(userId, submitId)
    }

    override fun getSubmits(competitionId: Long): Flow<SimpleSubmit> {
        return submitRepository.getSubmitRank(competitionId)
    }

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

    @InternalApi
    override suspend fun updateSubmit(id: Long, state: SubmitState, castMemory: Int?, castTime: Int?): Boolean {
        return submitRepository.updateSubmit(id, state, castMemory, castTime)
    }

}