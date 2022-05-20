package com.kairlec.koj.backend.service

import com.kairlec.koj.dao.extended.ListCondition
import com.kairlec.koj.dao.model.SimpleCompetition
import com.kairlec.koj.dao.repository.PageData
import com.kairlec.koj.dao.repository.UserType
import java.time.LocalDateTime

interface CompetitionService {
    suspend fun getCompetitions(
        userId: Long?,
        userType: UserType?,
        listCondition: ListCondition
    ): PageData<SimpleCompetition>

    suspend fun getCompetition(userType: UserType, id: Long): SimpleCompetition?

    suspend fun deleteCompetition(id: Long): Boolean

    suspend fun addCompetition(name: String, start: LocalDateTime, end: LocalDateTime, pwd: String?): Long?

    suspend fun joinCompetition(userId: Long, competitionId: Long, pwd: String?)

    suspend fun updateCompetition(id: Long, name: String?, pwd: String?): Boolean

    suspend fun addCompetitionProblem(id: Long, problemId: Long): Boolean

    suspend fun deleteCompetitionProblem(id: Long, problemId: Long): Boolean
}