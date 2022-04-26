package com.kairlec.koj.backend.controller.admin

import com.kairlec.koj.backend.service.ProblemService
import com.kairlec.koj.backend.util.sureEffect
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/admin")
class ProblemManagerController(
    private val problemService: ProblemService
) {
    @PutMapping("/problems/{problemId}/tags/{tagId}")
    suspend fun addProblemTag(
        @PathVariable problemId: Long,
        @PathVariable tagId: Long
    ): Boolean {
        return problemService.addProblemTag(problemId, tagId).sureEffect("add problem tag failed")
    }

    @DeleteMapping("/problems/{problemId}")
    suspend fun deleteProblem(
        @PathVariable problemId: Long
    ): Boolean {
        return problemService.removeProblem(problemId).sureEffect("delete problem failed")
    }

    @DeleteMapping("/problems/{problemId}/tags/{tagId}")
    suspend fun deleteProblemTag(
        @PathVariable problemId: Long,
        @PathVariable tagId: Long
    ): Boolean {
        return problemService.removeProblemTag(problemId, tagId).sureEffect("delete problem tag failed")
    }

    @PutMapping("/tags")
    suspend fun addTag(
        name: String
    ): Long {
        return problemService.newTag(name).sureEffect()
    }

    @PatchMapping("/tags/{tagId}")
    suspend fun updateTag(
        @PathVariable tagId: Long,
        name: String
    ): Boolean {
        return problemService.updateTag(tagId, name).sureEffect("update tag failed")
    }

    data class UpdateProblemModel(
        val name: String?,
        val content: String?,
        val spj: Boolean?,
    )

    @PatchMapping("/problems/{problemId}")
    suspend fun updateProblem(
        @PathVariable problemId: Long,
        updateModel: UpdateProblemModel
    ): Boolean {
        return problemService.updateProblem(problemId, updateModel.name, updateModel.content, updateModel.spj)
            .sureEffect("update problem failed")
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
        val maxOutputSize: Long?,
        val maxStack: Long?,
        val maxProcessNumber: Short?,
        val args: List<String>?,
        val env: List<String>?
    )

    @PutMapping("/problems/{problemId}/configs")
    suspend fun addConfig(
        @PathVariable problemId: Long,
        @RequestBody config: ConfigModel
    ) {
        problemService.addProblemConfig(
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

    @DeleteMapping("/problems/{problemId}/configs/{languageId}")
    suspend fun addConfig(
        @PathVariable problemId: Long,
        @PathVariable languageId: String
    ) {
        problemService.removeProblemConfig(
            problemId,
            languageId,
        ).sureEffect()
    }


}