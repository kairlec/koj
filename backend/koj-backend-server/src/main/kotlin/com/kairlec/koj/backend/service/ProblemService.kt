package com.kairlec.koj.backend.service

import com.kairlec.koj.dao.extended.ListCondition
import com.kairlec.koj.dao.model.Problem
import com.kairlec.koj.dao.model.SimpleProblem
import com.kairlec.koj.dao.repository.PageData
import kotlinx.coroutines.flow.Flow

interface ProblemService {
    suspend fun getProblems(listCondition: ListCondition): PageData<SimpleProblem>
    fun getProblems(competitionId: Long): Flow<SimpleProblem>
    suspend fun getProblem(id: Long): Problem?
}