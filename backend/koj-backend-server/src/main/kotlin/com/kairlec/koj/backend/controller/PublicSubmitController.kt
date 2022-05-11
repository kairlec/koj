package com.kairlec.koj.backend.controller

import com.kairlec.koj.backend.service.ReadOnlySubmitService
import com.kairlec.koj.backend.service.SubmitService
import com.kairlec.koj.backend.util.RE
import com.kairlec.koj.backend.util.currentListCondition
import com.kairlec.koj.backend.util.re
import com.kairlec.koj.dao.model.SimpleSubmit
import kotlinx.coroutines.flow.Flow
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/public/submits")
class PublicSubmitController(
    private val readOnlySubmitService: ReadOnlySubmitService,
    private val submitService: SubmitService?
) {
    @GetMapping("/-")
    suspend fun getSubmits(): RE<Flow<SimpleSubmit>> {
        return readOnlySubmitService.getSubmits(currentListCondition()).re()
    }


    @GetMapping("/languages/-")
    suspend fun getSupportLanguages(): List<String> {
        return submitService?.getLanguages() ?: emptyList()
    }
}
