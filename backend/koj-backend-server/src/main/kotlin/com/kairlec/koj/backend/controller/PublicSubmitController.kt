package com.kairlec.koj.backend.controller

import com.kairlec.koj.backend.service.ReadOnlySubmitService
import com.kairlec.koj.backend.service.SubmitService
import com.kairlec.koj.backend.util.RE
import com.kairlec.koj.backend.util.currentListCondition
import com.kairlec.koj.backend.util.re
import com.kairlec.koj.dao.model.SimpleSubmit
import kotlinx.coroutines.flow.Flow
import mu.KotlinLogging
import org.springframework.beans.factory.NoSuchBeanDefinitionException
import org.springframework.context.ApplicationContext
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/public/submits")
class PublicSubmitController(
    private val readOnlySubmitService: ReadOnlySubmitService,
    private val applicationContext: ApplicationContext
) {
    @GetMapping("/-")
    suspend fun getSubmits(): RE<Flow<SimpleSubmit>> {
        return readOnlySubmitService.getSubmits(currentListCondition()).re()
    }

    val submitService by lazy {
        try {
            applicationContext.getBean(SubmitService::class.java)
        } catch (e: NoSuchBeanDefinitionException) {
            null
        }
    }


    @GetMapping("/languages/-")
    suspend fun getSupportLanguages(): List<String> {
        if (submitService == null) {
            log.warn { "submit service is unavailable, return empty list." }
            return emptyList()
        }
        return submitService!!.getLanguages()
    }

    companion object {
        private val log = KotlinLogging.logger { }
    }
}
