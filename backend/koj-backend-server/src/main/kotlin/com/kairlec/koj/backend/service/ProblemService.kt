package com.kairlec.koj.backend.service

import com.kairlec.koj.dao.extended.ListCondition
import com.kairlec.koj.dao.model.Problem
import com.kairlec.koj.dao.model.SimpleProblem
import com.kairlec.koj.dao.repository.PageData
import com.kairlec.koj.dao.tables.records.ProblemTagRecord
import kotlinx.coroutines.flow.Flow

interface ProblemService {
    suspend fun getProblems(tags: List<String>, listCondition: ListCondition): PageData<SimpleProblem>
    suspend fun getTags(listCondition: ListCondition): PageData<ProblemTagRecord>
    suspend fun getProblems(userId: Long, competitionId: Long): Flow<SimpleProblem>
    suspend fun getProblem(id: Long): Problem?

    suspend fun newProblem(
        name: String,
        content: String,
        spj: Boolean,
        tags: List<Long>
    ): Long?

    suspend fun newTag(name: String): Long?

    suspend fun addProblemTag(problemId: Long, tagId: Long): Boolean

    suspend fun removeTag(tagId: Long): Boolean

    suspend fun removeProblemTag(problemId: Long, tagId: Long): Boolean

    suspend fun updateProblem(
        id: Long,
        name: String?,
        content: String?,
        spj: Boolean?,
    ): Boolean

    suspend fun removeProblem(id: Long): Boolean

    suspend fun updateTag(tagId: Long, name: String): Boolean

    suspend fun addProblemConfig(
        problemId: Long,
        languageId: String,
        time: Int,
        memory: Int,
        maxOutputSize: Long?,
        maxStack: Long?,
        maxProcessNumber: Short?,
        args: List<String>,
        env: List<String>
    ): Boolean

    suspend fun removeProblemConfig(
        problemId: Long,
        languageId: String,
    ): Boolean
}