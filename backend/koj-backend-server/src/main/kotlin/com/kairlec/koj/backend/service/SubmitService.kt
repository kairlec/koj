package com.kairlec.koj.backend.service

import com.kairlec.koj.common.InternalApi
import com.kairlec.koj.dao.extended.ListCondition
import com.kairlec.koj.dao.model.SimpleSubmit
import com.kairlec.koj.dao.model.SubmitDetail
import com.kairlec.koj.dao.model.SubmitState
import com.kairlec.koj.dao.repository.PageData
import kotlinx.coroutines.flow.Flow

interface SubmitService {
    suspend fun getSubmits(listCondition: ListCondition): PageData<SimpleSubmit>

    fun getSubmits(competitionId: Long): Flow<SimpleSubmit>

    suspend fun getSubmit(userId: Long, submitId: Long): SubmitDetail?

    suspend fun createSubmit(
        id: Long,
        userId: Long,
        competitionId: Long?,
        languageId: String,
        problemId: Long,
        code: String
    )
    @InternalApi
    suspend fun updateSubmit(id: Long, state: SubmitState, castMemory: Int? = null, castTime: Int? = null): Boolean

}