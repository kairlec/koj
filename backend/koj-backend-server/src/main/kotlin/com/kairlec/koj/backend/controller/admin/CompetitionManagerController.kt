package com.kairlec.koj.backend.controller.admin

import com.kairlec.koj.backend.service.CompetitionService
import com.kairlec.koj.backend.util.sureEffect
import com.kairlec.koj.backend.util.sureFound
import com.kairlec.koj.dao.tables.records.CompetitionRecord
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/admin/competitions")
class CompetitionManagerController(
    private val competitionService: CompetitionService
) {
    data class AddCompetitionModel(
        val name: String,
        val startTime: LocalDateTime,
        val endTime: LocalDateTime,
        val pwd: String?
    )

    @PutMapping("")
    suspend fun addCompetition(@RequestBody addCompetitionModel: AddCompetitionModel): Long {
        return competitionService.addCompetition(
            addCompetitionModel.name,
            addCompetitionModel.startTime,
            addCompetitionModel.endTime,
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
}