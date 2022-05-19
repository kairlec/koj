package com.kairlec.koj.backend.controller.internal

import com.kairlec.koj.backend.config.userIdAttributes
import com.kairlec.koj.backend.service.SubmitService
import com.kairlec.koj.backend.util.sureFound
import com.kairlec.koj.dao.model.SubmitDetail
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/submits")
class ReadOnlySubmitController(
    private val submitService: SubmitService
) {
    @GetMapping("/{submitId}")
    suspend fun getSubmit(
        @RequestAttribute(userIdAttributes) userId: Long,
        @PathVariable submitId: Long
    ): SubmitDetail {
        return submitService.getSubmit(userId, submitId).sureFound("submit<${submitId}> not found")
    }

    data class SubmitModel(
        val competitionId: Long?,
        val languageId: String,
        val problemId: Long,
        val code: String
    )

    @PutMapping("")
    suspend fun createSubmit(
        @RequestAttribute(userIdAttributes) userId: Long,
        @RequestBody submitModel: SubmitModel
    ) {
        submitService.createSubmit(
            userId,
            submitModel.competitionId,
            submitModel.languageId,
            submitModel.problemId,
            submitModel.code
        )
    }
}