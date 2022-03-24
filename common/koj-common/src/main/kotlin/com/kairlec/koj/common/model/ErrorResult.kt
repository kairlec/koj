package com.kairlec.koj.common.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.kairlec.koj.common.HttpStatusCode
import com.kairlec.koj.common.exception.GlobalErrorCode
import com.kairlec.koj.common.exception.GlobalException

/**
 * @author : Kairlec
 * @since : 2022/2/14
 **/
data class ErrorResult(
    val code: Int,
    var message: String,
    @JsonIgnore
    val httpStatusCode: HttpStatusCode = HttpStatusCode.Companion.Successful.OK,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var data: Any? = null,
) {

    companion object {
        @JvmStatic
        fun fromCode(e: GlobalErrorCode): ErrorResult {
            return ErrorResult(e.code, e.message, e.httpStatusCode)
        }

        @JvmStatic
        fun fromCode(e: GlobalErrorCode, cause: Throwable): ErrorResult {
            return ErrorResult(e.code, cause.message ?: e.message, e.httpStatusCode)
        }

        @JvmStatic
        fun fromCode(e: GlobalErrorCode, message: String? = null): ErrorResult {
            return if (message == null) {
                fromCode(e)
            } else {
                ErrorResult(e.code, message, e.httpStatusCode)
            }
        }

        @JvmStatic
        fun failed(e: Exception): ErrorResult {
            if (e is GlobalException) {
                return fromCode(e.errorCode, e)
            }
            return ErrorResult(
                GlobalErrorCode.AN_EXCEPTION_OCCURRED.code,
                e.message ?: "",
                GlobalErrorCode.AN_EXCEPTION_OCCURRED.httpStatusCode
            )
        }

        @JvmStatic
        fun failed(): ErrorResult {
            return ErrorResult(
                GlobalErrorCode.UNKNOWN_ERROR.code,
                GlobalErrorCode.UNKNOWN_ERROR.message,
                GlobalErrorCode.UNKNOWN_ERROR.httpStatusCode
            )
        }

        @JvmStatic
        fun failed(
            code: Int,
            message: String = "未知错误",
            httpStatusCode: HttpStatusCode = HttpStatusCode.Companion.ServerError.InternalServerError
        ): ErrorResult {
            return ErrorResult(code, message, httpStatusCode)
        }

        @JvmStatic
        fun failed(errorResult: ErrorResult): ErrorResult {
            return errorResult
        }

        @JvmStatic
        fun failed(message: String = "未知错误"): ErrorResult {
            return failed(
                GlobalErrorCode.UNKNOWN_ERROR.code,
                message,
                GlobalErrorCode.UNKNOWN_ERROR.httpStatusCode
            )
        }
    }
}

fun GlobalErrorCode.asErrorResult() = ErrorResult.fromCode(this)
