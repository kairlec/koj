package com.kairlec.koj.backend.service

import com.kairlec.koj.common.InternalApi
import com.kairlec.koj.dao.extended.ListCondition
import com.kairlec.koj.dao.model.Problem
import com.kairlec.koj.dao.model.SimpleProblem
import com.kairlec.koj.dao.repository.PageData
import com.kairlec.koj.dao.repository.UserType
import com.kairlec.koj.dao.tables.records.ProblemConfigRecord
import com.kairlec.koj.dao.tables.records.ProblemRunRecord
import com.kairlec.koj.dao.tables.records.ProblemTagRecord
import kotlinx.coroutines.flow.Flow
import java.math.BigInteger

interface ProblemService {
    suspend fun getProblems(tags: List<String>, listCondition: ListCondition): PageData<SimpleProblem>
    suspend fun getTags(listCondition: ListCondition): PageData<ProblemTagRecord>
    suspend fun getProblems(userId: Long, userType: UserType, competitionId: Long): Flow<SimpleProblem>
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
        tags: List<Long>?
    ): Boolean

    suspend fun removeProblem(id: Long): Boolean

    suspend fun updateTag(tagId: Long, name: String): Boolean

    suspend fun saveProblemConfig(
        problemId: Long,
        languageId: String,
        time: Int,
        memory: Int,
        maxOutputSize: BigInteger?,
        maxStack: BigInteger?,
        maxProcessNumber: Int?,
        args: List<String>,
        env: List<String>
    ): Boolean

    fun getProblemConfig(
        problemId: Long,
    ): Flow<ProblemConfigRecord>

    suspend fun removeProblemConfig(
        problemId: Long,
        languageId: String,
    ): Boolean

    suspend fun saveProblemRunConfig(
        problemId: Long,
        stdin: String,
        ansout: String
    ): Boolean

    suspend fun getProblemRunConfig(
        problemId: Long
    ): ProblemRunRecord?

    @OptIn(InternalApi::class)
    suspend fun getProblemAnsout(
        problemId: Long
    ): String?
}