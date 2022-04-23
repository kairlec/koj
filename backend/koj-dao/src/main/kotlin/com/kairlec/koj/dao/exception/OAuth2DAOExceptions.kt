package com.kairlec.koj.dao.exception

import com.kairlec.koj.common.exception.GlobalErrorCode
import com.kairlec.koj.common.exception.GlobalException

/**
 * @author : Kairlec
 * @since : 2022/1/7
 **/
sealed class KOJDAOException @JvmOverloads constructor(
    override val errorCode: GlobalErrorCode,
    override val message: String,
    override val cause: Throwable? = null
) : GlobalException(errorCode, message, cause) {
    companion object {
        private const val serialVersionUID: Long = -3627965474993764316L
    }
}

class UsernameExistsException @JvmOverloads constructor(
    val username: String,
    override val message: String = "username has exists:$username",
    override val cause: Throwable? = null
) : KOJDAOException(GlobalErrorCode.USERNAME_EXISTS, message, cause) {
    companion object {
        private const val serialVersionUID: Long = -5507898284671234017L
    }
}

class UserCreateException @JvmOverloads constructor(
    val username: String,
    val email: String,
    val phone: String?,
    override val message: String = "user create failed:$username(${email} , ${phone})",
    override val cause: Throwable? = null
) : KOJDAOException(GlobalErrorCode.USER_CREATE_FAILED, message, cause) {
    companion object {
        private const val serialVersionUID: Long = -6002555278072796945L
    }
}

class CreateSubmitException(
    override val message: String = "submit create failed",
    override val cause: Throwable? = null
) : KOJDAOException(GlobalErrorCode.CREATE_SUBMIT_FAILED, message, cause)

class CreateCodeRecordException(
    override val message: String = "code record create failed",
    override val cause: Throwable? = null
) : KOJDAOException(GlobalErrorCode.CREATE_CODE_RECORD_FAILED, message, cause)

class NoSuchContentException(
    override val message: String,
    override val cause: Throwable? = null
) : KOJDAOException(GlobalErrorCode.CONTENT_NOT_FOUND, message, cause)

class CompetitionPwdWrongException(
    override val message: String = "competition password is wrong",
    override val cause: Throwable? = null
) : KOJDAOException(GlobalErrorCode.COMPETITION_PWD_ERROR, message, cause)

class CompetitionNotStartedYetException(override val message: String, override val cause: Throwable? = null) :
    KOJDAOException(GlobalErrorCode.COMPETITION_NOT_STARTED_YET, message, cause = cause)

class CompetitionFrozenException(override val message: String, override val cause: Throwable? = null) :
    KOJDAOException(GlobalErrorCode.COMPETITION_FROZEN, message, cause = cause)

class CompetitionOverException(override val message: String, override val cause: Throwable? = null) :
    KOJDAOException(GlobalErrorCode.COMPETITION_OVER, message, cause = cause)
