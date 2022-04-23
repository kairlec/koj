package com.kairlec.koj.backend.exp

import com.kairlec.koj.common.exception.GlobalErrorCode
import com.kairlec.koj.common.exception.GlobalException
import kotlin.time.Duration

class DataNotFoundException(override val message: String, override val cause: Throwable? = null) :
    GlobalException(GlobalErrorCode.CONTENT_NOT_FOUND, message, cause = cause)

class DataNotModifiedException(override val message: String, override val cause: Throwable? = null) :
    GlobalException(GlobalErrorCode.CONTENT_NOT_MODIFIED, message, cause = cause)

class PermissionDeniedException(override val message: String, override val cause: Throwable? = null) :
    GlobalException(GlobalErrorCode.PERMISSION_DENIED, message, cause = cause)

class MailSendTimeLimitException(val leftTime: Duration) : GlobalException(GlobalErrorCode.MAIL_SEND_TIME_LIMIT)

class ResetPasswordCodeWrongException : GlobalException(GlobalErrorCode.VERIFY_CODE_ERROR)