package com.kairlec.koj.backend.service.impl

import com.kairlec.koj.backend.service.CompetitionService
import com.kairlec.koj.dao.extended.ListCondition
import com.kairlec.koj.dao.model.SimpleCompetition
import com.kairlec.koj.dao.repository.CompetitionRepository
import com.kairlec.koj.dao.repository.PageData
import com.kairlec.koj.dao.repository.UserType
import com.kairlec.koj.dao.tables.records.CompetitionRecord
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class CompetitionServiceImpl(
    private val competitionRepository: CompetitionRepository
) : CompetitionService {
    override suspend fun getCompetitions(
        userId: Long?,
        userType: UserType?,
        listCondition: ListCondition
    ): PageData<SimpleCompetition> {
        return competitionRepository.getCompetitions(userId, userType, listCondition)
    }

    override suspend fun getCompetition(id: Long): CompetitionRecord? {
        return competitionRepository.getCompetition(id)
    }

    override suspend fun deleteCompetition(id: Long): Boolean {
        return competitionRepository.deleteCompetition(id)
    }

    override suspend fun addCompetition(name: String, start: LocalDateTime, end: LocalDateTime, pwd: String?): Long? {
        return competitionRepository.createCompetition(name, pwd, start, end)
    }

    override suspend fun joinCompetition(userId: Long, competitionId: Long, pwd: String?) {
        return competitionRepository.joinCompetition(userId, competitionId, pwd)
    }

    override suspend fun updateCompetition(id: Long, name: String?, pwd: String?): Boolean {
        return competitionRepository.updateCompetition(id, name, pwd)
    }

    override suspend fun addCompetitionProblem(
        id: Long,
        problemId: Long,
    ): Boolean {
        return competitionRepository.addCompetitionProblem(id, problemId)
    }

    override suspend fun deleteCompetitionProblem(
        id: Long,
        problemId: Long,
    ): Boolean {
        return competitionRepository.deleteCompetitionProblem(id, problemId)
    }

}