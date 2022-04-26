package com.kairlec.koj.backend.service

import com.kairlec.koj.dao.extended.ListCondition
import com.kairlec.koj.dao.model.SimpleSubmit
import com.kairlec.koj.dao.model.SubmitDetail
import com.kairlec.koj.dao.repository.PageData
import kotlinx.coroutines.flow.Flow

interface ReadOnlySubmitService {
    suspend fun getSubmits(listCondition: ListCondition): PageData<SimpleSubmit>

    suspend fun getSubmits(userId: Long, competitionId: Long): Flow<SimpleSubmit>

    suspend fun getSubmit(userId: Long, submitId: Long): SubmitDetail?
}