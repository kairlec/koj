package com.kairlec.koj.backend.service

import com.kairlec.koj.dao.extended.ListCondition
import com.kairlec.koj.dao.repository.PageData
import com.kairlec.koj.dao.tables.records.CompetitionRecord
import java.time.LocalDateTime

interface CompetitionService {
    suspend fun getCompetitions(listCondition: ListCondition): PageData<CompetitionRecord>

    suspend fun getCompetition(id: Long): CompetitionRecord?

    suspend fun addCompetition(name: String, start: LocalDateTime, end: LocalDateTime, pwd: String?): Long?

    suspend fun joinCompetition(userId: Long, competitionId: Long, pwd: String?)

    suspend fun updateCompetition(id: Long, name: String?, pwd: String?): Boolean
}