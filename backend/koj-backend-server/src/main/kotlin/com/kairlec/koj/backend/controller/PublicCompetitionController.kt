package com.kairlec.koj.backend.controller

import com.kairlec.koj.backend.config.userIdAttributes
import com.kairlec.koj.backend.config.userTypeAttributes
import com.kairlec.koj.backend.service.CompetitionService
import com.kairlec.koj.backend.util.RE
import com.kairlec.koj.backend.util.currentListCondition
import com.kairlec.koj.backend.util.re
import com.kairlec.koj.dao.model.SimpleCompetition
import com.kairlec.koj.dao.repository.UserType
import kotlinx.coroutines.flow.Flow
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestAttribute
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/public/competitions")
class PublicCompetitionController(
    private val competitionService: CompetitionService
) {

    @GetMapping("/-")
    suspend fun getCompetitions(
        @RequestAttribute(userIdAttributes) userId: Long?,
        @RequestAttribute(userTypeAttributes) userType: UserType?
    ): RE<Flow<SimpleCompetition>> {
        return competitionService.getCompetitions(userId, userType, currentListCondition()).re()
    }
}