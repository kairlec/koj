package com.kairlec.koj.backend.controller.admin

import com.kairlec.koj.backend.service.CompetitionService
import com.kairlec.koj.backend.util.sureEffect
import com.kairlec.koj.backend.util.sureFound
import com.kairlec.koj.dao.tables.records.CompetitionRecord
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime

@RestController
@RequestMapping("/admin/competitions")
class CompetitionManagerController(
    private val competitionService: CompetitionService
) {
    data class AddCompetitionModel(
        val name: String,
        val startTime: OffsetDateTime,
        val endTime: OffsetDateTime,
        val pwd: String?
    )

    @PutMapping("")
    suspend fun addCompetition(@RequestBody addCompetitionModel: AddCompetitionModel): Long {
        return competitionService.addCompetition(
            addCompetitionModel.name,
            addCompetitionModel.startTime.atZoneSameInstant(ZoneOffset.UTC).toLocalDateTime(),
            addCompetitionModel.endTime.atZoneSameInstant(ZoneOffset.UTC).toLocalDateTime(),
            addCompetitionModel.pwd
        ).sureEffect()
    }

    @GetMapping("/{id}")
    suspend fun getCompetition(@PathVariable id: Long): CompetitionRecord {
        return competitionService.getCompetition(id).sureFound()
    }

    @DeleteMapping("/{id}")
    suspend fun deleteCompetition(@PathVariable id: Long) {
        competitionService.deleteCompetition(id).sureEffect()
    }

    data class UpdateCompetitionModel(
        val name: String?,
        val pwd: String?
    )

    @PatchMapping("/{id}")
    suspend fun updateCompetition(@PathVariable id: Long, @RequestBody updateCompetitionModel: UpdateCompetitionModel) {
        competitionService.updateCompetition(id, updateCompetitionModel.name, updateCompetitionModel.pwd).sureEffect()
    }

    @PutMapping("/{id}/problems/{problemId}")
    suspend fun addProblemToCompetition(@PathVariable id: Long, @PathVariable problemId: Long) {
        competitionService.addCompetitionProblem(id, problemId).sureEffect()
    }

    @DeleteMapping("/{id}/problems/{problemId}")
    suspend fun removeProblemFromCompetition(@PathVariable id: Long, @PathVariable problemId: Long) {
        competitionService.deleteCompetitionProblem(id, problemId).sureEffect()
    }
}