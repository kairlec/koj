package com.kairlec.koj.backend.controller.internal

import com.kairlec.koj.backend.config.userIdAttributes
import com.kairlec.koj.backend.service.CompetitionService
import com.kairlec.koj.backend.service.ReadOnlySubmitService
import com.kairlec.koj.dao.model.SimpleSubmit
import kotlinx.coroutines.flow.Flow
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/competitions")
class CompetitionController(
    private val competitionService: CompetitionService,
    private val submitService: ReadOnlySubmitService
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
        @RequestAttribute(userIdAttributes) userId: Long
    ): Flow<SimpleSubmit> {
        return submitService.getSubmits(userId, competitionId)
    }

}