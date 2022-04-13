package com.kairlec.koj.backend.service.impl

import com.kairlec.koj.backend.service.ProblemService
import com.kairlec.koj.dao.extended.ListCondition
import com.kairlec.koj.dao.model.Problem
import com.kairlec.koj.dao.model.SimpleProblem
import com.kairlec.koj.dao.repository.PageData
import com.kairlec.koj.dao.repository.ProblemRepository
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service

@Service
class ProblemServiceImpl(
    private val problemRepository: ProblemRepository
) : ProblemService {
    override suspend fun getProblems(listCondition: ListCondition): PageData<SimpleProblem> {
        return problemRepository.getProblems(listCondition)
    }

    override fun getProblems(competitionId: Long): Flow<SimpleProblem> {
        return problemRepository.getProblems(competitionId)
    }

    override suspend fun getProblem(id: Long): Problem? {
        return problemRepository.getProblem(id)
    }
}