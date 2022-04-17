package com.kairlec.koj.backend.exp

import com.kairlec.koj.common.exception.GlobalErrorCode
import com.kairlec.koj.common.exception.GlobalException

class DataNotFoundException(override val message: String, override val cause: Throwable? = null) :
    GlobalException(GlobalErrorCode.CONTENT_NOT_FOUND, message, cause = cause)

class DataNotModifiedException(override val message: String, override val cause: Throwable? = null) :
    GlobalException(GlobalErrorCode.CONTENT_NOT_MODIFIED, message, cause = cause)

class PermissionDeniedException(override val message: String, override val cause: Throwable? = null) :
    GlobalException(GlobalErrorCode.PERMISSION_DENIED, message, cause = cause)

class CompetitionNotStartedYetException(override val message: String, override val cause: Throwable? = null) :
    GlobalException(GlobalErrorCode.COMPETITION_NOT_STARTED_YET, message, cause = cause)

class CompetitionFrozenException(override val message: String, override val cause: Throwable? = null) :
    GlobalException(GlobalErrorCode.COMPETITION_FROZEN, message, cause = cause)
class CompetitionOverException(override val message: String, override val cause: Throwable? = null) :
    GlobalException(GlobalErrorCode.COMPETITION_OVER, message, cause = cause)