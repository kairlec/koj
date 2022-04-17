package com.kairlec.koj.backend.controller.internal

import com.kairlec.koj.backend.config.userIdAttributes
import com.kairlec.koj.backend.service.ProblemService
import com.kairlec.koj.dao.model.SimpleProblem
import kotlinx.coroutines.flow.Flow
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestAttribute
import org.springframework.web.bind.annotation.RestController

@RestController
class ProblemController(
    private val problemService: ProblemService
) {
    @GetMapping("/competitions/{competitionId}/problems/-")
    suspend fun getProblems(
        @RequestAttribute(userIdAttributes) userId: Long,
        @PathVariable competitionId: Long
    ): Flow<SimpleProblem> {
        return problemService.getProblems(userId, competitionId)
    }
}