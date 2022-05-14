package com.kairlec.koj.backend.service

import com.kairlec.koj.common.InternalApi
import com.kairlec.koj.dao.model.SubmitState

interface SubmitService {
    @InternalApi
    suspend fun createSubmit(
        id: Long,
        userId: Long,
        competitionId: Long?,
        languageId: String,
        problemId: Long,
        code: String
    )

    suspend fun createSubmit(
        userId: Long,
        competitionId: Long?,
        languageId: String,
        problemId: Long,
        code: String
    ): Long

    @InternalApi
    suspend fun updateSubmit(
        id: Long,
        state: SubmitState,
        castMemory: Long? = null,
        castTime: Long? = null,
        stderr: String? = null,
        stdout: String? = null
    ): Boolean

    @InternalApi
    suspend fun getProblemIdOfSubmit(
        id: Long,
    ): Long?

    fun getLanguages(): List<String>
}