package com.kairlec.koj.backend.controller.internal

import com.kairlec.koj.backend.config.userIdAttributes
import com.kairlec.koj.backend.config.userTypeAttributes
import com.kairlec.koj.backend.service.CompetitionService
import com.kairlec.koj.backend.service.ProblemService
import com.kairlec.koj.backend.service.SubmitService
import com.kairlec.koj.dao.model.SimpleProblem
import com.kairlec.koj.dao.model.SimpleSubmit
import com.kairlec.koj.dao.repository.UserType
import kotlinx.coroutines.flow.Flow
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/competitions")
class CompetitionController(
    private val competitionService: CompetitionService,
    private val submitService: SubmitService,
    private val problemService: ProblemService
) {
    @PostMapping("/{competitionId}:join")
    suspend fun joinCompetition(
        @PathVariable competitionId: Long,
        @RequestAttribute(userIdAttributes) userId: Long,
        password: String?
    ) {
        return competitionService.joinCompetition(userId, competitionId, password)
    }

    @GetMapping("/{competitionId}/submits/-")
    suspend fun getCompetitionSubmits(
        @PathVariable competitionId: Long,
        @RequestAttribute(userIdAttributes) userId: Long,
        @RequestAttribute(userTypeAttributes) userType: UserType
    ): Flow<SimpleSubmit> {
        return submitService.getSubmits(userId, userType, competitionId)
    }

    @GetMapping("/{competitionId}/problems/-")
    suspend fun getProblems(
        @RequestAttribute(userIdAttributes) userId: Long,
        @RequestAttribute(userTypeAttributes) userType: UserType,
        @PathVariable competitionId: Long
    ): Flow<SimpleProblem> {
        return problemService.getProblems(userId, userType, competitionId)
    }

}