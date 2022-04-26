package com.kairlec.koj.backend.service.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.kairlec.koj.backend.exp.PermissionDeniedException
import com.kairlec.koj.backend.service.ProblemService
import com.kairlec.koj.backend.util.sureFound
import com.kairlec.koj.dao.exception.CompetitionNotStartedYetException
import com.kairlec.koj.dao.extended.ListCondition
import com.kairlec.koj.dao.model.Problem
import com.kairlec.koj.dao.model.SimpleProblem
import com.kairlec.koj.dao.repository.CompetitionRepository
import com.kairlec.koj.dao.repository.PageData
import com.kairlec.koj.dao.repository.ProblemRepository
import com.kairlec.koj.dao.tables.records.ProblemRunRecord
import com.kairlec.koj.dao.tables.records.ProblemTagRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class ProblemServiceImpl(
    private val problemRepository: ProblemRepository,
    private val competitionRepository: CompetitionRepository,
    private val objectMapper: ObjectMapper,
) : ProblemService {
    override suspend fun getProblems(tags: List<String>, listCondition: ListCondition): PageData<SimpleProblem> {
        return problemRepository.getProblems(tags, listCondition)
    }

    override suspend fun getTags(listCondition: ListCondition): PageData<ProblemTagRecord> {
        return problemRepository.getTags(listCondition)
    }

    override suspend fun getProblems(userId: Long, competitionId: Long): Flow<SimpleProblem> {
        val competition =
            competitionRepository.getCompetition(competitionId).sureFound("cannot find competition<${competitionId}>")
        val inCompetition = competitionRepository.isInCompetition(userId, competitionId)
        if (!inCompetition) {
            throw PermissionDeniedException("user<${userId}> is not in competition<${competitionId}>")
        }
        if (competition.startTime.isAfter(LocalDateTime.now())) {
            throw CompetitionNotStartedYetException("competition<${competitionId}> has not started yet")
        }
        return withContext(Dispatchers.IO) {
            problemRepository.getProblems(competitionId)
        }
    }

    override suspend fun getProblem(id: Long): Problem? {
        return problemRepository.getProblem(id)
    }

    @Transactional(rollbackFor = [Exception::class])
    override suspend fun newProblem(name: String, content: String, spj: Boolean, tags: List<Long>): Long? {
        return problemRepository.newProblem(name, content, spj).also {
            if (it != null && tags.isNotEmpty()) {
                problemRepository.addProblemTags(it, tags)
            }
        }
    }

    override suspend fun newTag(name: String): Long? {
        return problemRepository.newTag(name)
    }

    override suspend fun addProblemTag(problemId: Long, tagId: Long): Boolean {
        return problemRepository.addProblemTag(problemId, tagId)
    }

    override suspend fun removeTag(tagId: Long): Boolean {
        return problemRepository.removeTag(tagId)
    }

    override suspend fun removeProblemTag(problemId: Long, tagId: Long): Boolean {
        return problemRepository.removeProblemTag(problemId, tagId)
    }

    override suspend fun updateProblem(id: Long, name: String?, content: String?, spj: Boolean?): Boolean {
        return problemRepository.updateProblem(id, name, content, spj)
    }

    override suspend fun removeProblem(id: Long): Boolean {
        return problemRepository.removeProblem(id)
    }

    override suspend fun updateTag(tagId: Long, name: String): Boolean {
        return problemRepository.updateTag(tagId, name)
    }

    override suspend fun addProblemConfig(
        problemId: Long,
        languageId: String,
        time: Int,
        memory: Int,
        maxOutputSize: Long?,
        maxStack: Long?,
        maxProcessNumber: Short?,
        args: List<String>,
        env: List<String>
    ): Boolean {
        return problemRepository.addProblemConfig(
            problemId,
            languageId,
            time,
            memory,
            maxOutputSize,
            maxStack,
            maxProcessNumber,
            objectMapper.writeValueAsString(args),
            objectMapper.writeValueAsString(env)
        )
    }

    override suspend fun removeProblemConfig(
        problemId: Long,
        languageId: String,
    ): Boolean {
        return problemRepository.removeProblemConfig(
            problemId,
            languageId,
        )
    }

    override suspend fun saveProblemRunConfig(problemId: Long, stdin: String, ansout: String): Boolean {
        return problemRepository.saveProblemRunConfig(problemId, stdin, ansout)
    }

    override suspend fun getProblemRunConfig(problemId: Long): ProblemRunRecord? {
        return problemRepository.getProblemRunConfig(problemId)
    }
}