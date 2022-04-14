package com.kairlec.koj.backend.service.impl

import com.kairlec.koj.backend.service.ProblemService
import com.kairlec.koj.dao.extended.ListCondition
import com.kairlec.koj.dao.model.Problem
import com.kairlec.koj.dao.model.SimpleProblem
import com.kairlec.koj.dao.repository.PageData
import com.kairlec.koj.dao.repository.ProblemRepository
import com.kairlec.koj.dao.tables.records.ProblemTagRecord
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service

@Service
class ProblemServiceImpl(
    private val problemRepository: ProblemRepository
) : ProblemService {
    override suspend fun getProblems(tags: List<String>, listCondition: ListCondition): PageData<SimpleProblem> {
        return problemRepository.getProblems(tags, listCondition)
    }

    override suspend fun getTags(listCondition: ListCondition): PageData<ProblemTagRecord> {
        return problemRepository.getTags(listCondition)
    }

    override fun getProblems(competitionId: Long): Flow<SimpleProblem> {
        return problemRepository.getProblems(competitionId)
    }

    override suspend fun getProblem(id: Long): Problem? {
        return problemRepository.getProblem(id)
    }

    override suspend fun newProblem(name: String, content: String, spj: Boolean): Long? {
        return problemRepository.newProblem(name, content, spj)
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
}