package com.kairlec.koj.backend.controller.internal

import com.kairlec.koj.backend.component.LanguageIdSupporter
import com.kairlec.koj.backend.config.SandboxMQ
import com.kairlec.koj.backend.config.userIdAttributes
import com.kairlec.koj.backend.service.ReadOnlySubmitService
import com.kairlec.koj.backend.service.SubmitService
import com.kairlec.koj.backend.util.sureFound
import com.kairlec.koj.dao.model.SubmitDetail
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.web.bind.annotation.*
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping

@RestController
@RequestMapping("/submits")
class ReadOnlySubmitController(
    private val submitService: ReadOnlySubmitService
) {
    @GetMapping("/{submitId}")
    suspend fun getSubmit(
        @RequestAttribute(userIdAttributes) userId: Long,
        @PathVariable submitId: Long
    ): SubmitDetail {
        return submitService.getSubmit(userId, submitId).sureFound("submit<${submitId}> not found")
    }
}

@RestController
@RequestMapping("/submits")
// TODO: [SubmitService]不知道为什么找得到
@ConditionalOnBean(SubmitService::class, SandboxMQ::class, LanguageIdSupporter::class)
class SubmitController(
    private val submitService: SubmitService
) {
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

    @GetMapping("/languages/-")
    suspend fun getSupportLanguages(): List<String> {
        return submitService.getLanguages()
    }
}