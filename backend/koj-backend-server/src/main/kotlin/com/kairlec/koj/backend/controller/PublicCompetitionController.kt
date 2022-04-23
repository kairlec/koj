package com.kairlec.koj.backend.controller

import com.kairlec.koj.backend.service.CompetitionService
import com.kairlec.koj.backend.util.RE
import com.kairlec.koj.backend.util.currentListCondition
import com.kairlec.koj.backend.util.re
import com.kairlec.koj.dao.tables.records.CompetitionRecord
import kotlinx.coroutines.flow.Flow
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
@RequestMapping("/public/competitions")
class PublicCompetitionController(
    private val competitionService: CompetitionService
) {
    data class CompetitionVO(
        val id: Long,
        val name: String,
        val startTime: LocalDateTime,
        val endTime: LocalDateTime
    )

    fun CompetitionRecord.toVO(): CompetitionVO {
        return CompetitionVO(
            id,
            name,
            startTime,
            endTime
        )
    }

    @GetMapping("/-")
    suspend fun getCompetitions(): RE<Flow<CompetitionVO>> {
        return competitionService.getCompetitions(currentListCondition()).re { it.toVO() }
    }
}