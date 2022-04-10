package com.kairlec.koj.backend.controller

import com.kairlec.koj.backend.util.RE
import com.kairlec.koj.backend.util.status
import com.kairlec.koj.common.exception.GlobalException
import com.kairlec.koj.common.model.ErrorResult
import com.kairlec.koj.common.model.asErrorResult
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler
    fun handleGlobalException(e: GlobalException): RE<ErrorResult> {
        log.error(e) { "GlobalExceptionHandler: ${e.message}" }
        return e.errorCode.let {
            it.asErrorResult().status(HttpStatus.valueOf(it.httpStatusCode.code))
        }
    }

    companion object {
        private val log = KotlinLogging.logger { }
    }
}