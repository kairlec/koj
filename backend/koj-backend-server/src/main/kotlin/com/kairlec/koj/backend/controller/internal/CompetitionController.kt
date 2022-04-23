package com.kairlec.koj.backend.controller.internal

import com.kairlec.koj.backend.config.userIdAttributes
import com.kairlec.koj.backend.service.CompetitionService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/competitions")
class CompetitionController(
    private val competitionService: CompetitionService
) {
    @PostMapping("/{competitionId}:join")
    suspend fun joinCompetition(
        @PathVariable competitionId: Long,
        @RequestAttribute(userIdAttributes) userId: Long,
        @ModelAttribute password: String?
    ) {
        return competitionService.joinCompetition(userId, competitionId, password)
    }
}