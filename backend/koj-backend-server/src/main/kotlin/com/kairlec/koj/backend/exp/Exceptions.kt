package com.kairlec.koj.backend.exp

import com.kairlec.koj.common.exception.GlobalErrorCode
import com.kairlec.koj.common.exception.GlobalException

class DataNotFoundException(override val message: String, override val cause: Throwable? = null) :
    GlobalException(GlobalErrorCode.CONTENT_NOT_FOUND, message, cause = cause)

class DataNotModifiedException(override val message: String, override val cause: Throwable? = null) :
    GlobalException(GlobalErrorCode.CONTENT_NOT_MODIFIED, message, cause = cause)