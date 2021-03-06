package com.kairlec.koj.backend.controller.admin

import com.kairlec.koj.backend.service.ProblemService
import com.kairlec.koj.backend.util.sureEffect
import com.kairlec.koj.backend.util.sureFound
import com.kairlec.koj.dao.tables.records.ProblemConfigRecord
import com.kairlec.koj.dao.tables.records.ProblemRunRecord
import kotlinx.coroutines.flow.Flow
import org.springframework.web.bind.annotation.*
import java.math.BigInteger

@RestController
@RequestMapping("/admin")
class ProblemManagerController(
    private val problemService: ProblemService
) {
    @PutMapping("/problems/{problemId}/tags/{tagId}")
    suspend fun addProblemTag(
        @PathVariable problemId: Long,
        @PathVariable tagId: Long
    ) {
        problemService.addProblemTag(problemId, tagId).sureEffect("add problem tag failed")
    }

    @DeleteMapping("/problems/{problemId}/tags/{tagId}")
    suspend fun deleteProblemTag(
        @PathVariable problemId: Long,
        @PathVariable tagId: Long
    ) {
        problemService.removeProblemTag(problemId, tagId).sureEffect("delete problem tag failed")
    }

    @PutMapping("/tags")
    suspend fun addTag(
        name: String
    ): Long {
        return problemService.newTag(name).sureEffect()
    }

    @DeleteMapping("/tags/{tagId}")
    suspend fun removeTag(
        @PathVariable tagId: Long
    ) {
        problemService.removeTag(tagId).sureEffect()
    }

    @PatchMapping("/tags/{tagId}")
    suspend fun updateTag(
        @PathVariable tagId: Long,
        name: String
    ) {
        problemService.updateTag(tagId, name).sureEffect("update tag failed")
    }

    data class UpdateProblemModel(
        val name: String?,
        val content: String?,
        val spj: Boolean?,
        val tags: List<Long>?
    )

    @PatchMapping("/problems/{problemId}")
    suspend fun updateProblem(
        @PathVariable problemId: Long,
        @RequestBody updateModel: UpdateProblemModel
    ) {
        problemService.updateProblem(
            problemId,
            updateModel.name,
            updateModel.content,
            updateModel.spj,
            updateModel.tags
        ).sureEffect("update problem failed")
    }

    @DeleteMapping("/problems/{problemId}")
    suspend fun deleteProblem(
        @PathVariable problemId: Long
    ) {
        problemService.removeProblem(problemId).sureEffect("delete problem failed")
    }

    data class ProblemModel(
        val name: String,
        val content: String,
        val spj: Boolean,
        val tags: List<Long>
    )

    @PutMapping("/problems")
    suspend fun addProblem(
        @RequestBody problem: ProblemModel
    ): Long {
        if (problem.spj) {
            throw IllegalStateException("spj not support yet")
        }
        return problemService.newProblem(problem.name, problem.content, problem.spj, problem.tags).sureEffect()
    }

    data class ConfigModel(
        val languageId: String,
        val time: Int,
        val memory: Int,
        val maxOutputSize: BigInteger?,
        val maxStack: BigInteger?,
        val maxProcessNumber: Int?,
        val args: List<String>?,
        val env: List<String>?
    )

    @PutMapping("/problems/{problemId}/configs")
    suspend fun saveConfig(
        @PathVariable problemId: Long,
        @RequestBody config: ConfigModel
    ) {
        problemService.saveProblemConfig(
            problemId,
            config.languageId,
            config.time,
            config.memory,
            config.maxOutputSize,
            config.maxStack,
            config.maxProcessNumber,
            config.args ?: emptyList(),
            config.env ?: emptyList()
        ).sureEffect()
    }

    @GetMapping("/problems/{problemId}/configs/-")
    fun getConfigs(
        @PathVariable problemId: Long,
    ): Flow<ProblemConfigRecord> {
        return problemService.getProblemConfig(problemId)
    }

    @DeleteMapping("/problems/{problemId}/configs/{languageId}")
    suspend fun deleteConfig(
        @PathVariable problemId: Long,
        @PathVariable languageId: String
    ) {
        problemService.removeProblemConfig(
            problemId,
            languageId,
        ).sureEffect()
    }

    data class ProblemRunConfig(
        val stdin: String,
        val ansout: String
    )

    @PutMapping("/problems/{problemId}/runs")
    suspend fun saveRunConfig(
        @PathVariable problemId: Long,
        @RequestBody config: ProblemRunConfig
    ) {
        problemService.saveProblemRunConfig(problemId, config.stdin, config.ansout).sureEffect()
    }

    @GetMapping("/problems/{problemId}/runs")
    suspend fun getRunConfig(
        @PathVariable problemId: Long
    ): ProblemRunRecord {
        return problemService.getProblemRunConfig(problemId).sureFound("run config not set yet")
    }

}