package com.kairlec.koj.backend.service.impl

import com.kairlec.koj.backend.exp.PermissionDeniedException
import com.kairlec.koj.backend.service.ReadOnlySubmitService
import com.kairlec.koj.dao.extended.ListCondition
import com.kairlec.koj.dao.model.SimpleSubmit
import com.kairlec.koj.dao.model.SubmitDetail
import com.kairlec.koj.dao.repository.CompetitionRepository
import com.kairlec.koj.dao.repository.PageData
import com.kairlec.koj.dao.repository.SubmitRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service

@Service
class ReadOnlySubmitServiceImpl(
    private val submitRepository: SubmitRepository,
    private val competitionRepository: CompetitionRepository,
): ReadOnlySubmitService {
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
}