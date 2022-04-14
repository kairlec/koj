package com.kairlec.koj.backend.controller

import com.kairlec.koj.backend.service.ProblemService
import com.kairlec.koj.backend.util.RE
import com.kairlec.koj.backend.util.currentListCondition
import com.kairlec.koj.backend.util.re
import com.kairlec.koj.backend.util.sureFound
import com.kairlec.koj.dao.model.Problem
import com.kairlec.koj.dao.model.SimpleProblem
import com.kairlec.koj.dao.tables.records.ProblemTagRecord
import kotlinx.coroutines.flow.Flow
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/public")
class PublicProblemController(
    private val problemService: ProblemService
) {
    @GetMapping("/problems/-")
    suspend fun getProblems(
        @RequestParam tags: List<String>
    ): RE<Flow<SimpleProblem>> {
        return problemService.getProblems(tags, currentListCondition()).re()
    }

    @GetMapping("/competitions/{competitionId}/problems/-")
    fun getProblems(
        @PathVariable competitionId: Long
    ): Flow<SimpleProblem> {
        return problemService.getProblems(competitionId)
    }

    @GetMapping("/tags/-")
    suspend fun getTags(): RE<Flow<ProblemTagRecord>> {
        return problemService.getTags(currentListCondition()).re()
    }

    @GetMapping("/problems/{problemId}")
    suspend fun getProblem(
        @PathVariable problemId: Long
    ): Problem {
        return problemService.getProblem(problemId).sureFound("ProblemId<${problemId}> not found")
    }



}