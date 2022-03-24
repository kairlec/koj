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
