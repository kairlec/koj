@file:Suppress("unused")

package com.kairlec.koj.common.exception

/**
 * @author : Kairlec
 * @since : 2022/2/14
 **/

class AccountNotLoginException : GlobalException(GlobalErrorCode.ACCOUNT_NOT_LOGIN) {
    companion object {
        private const val serialVersionUID: Long = -5960118262347796623L
    }
}

class UserDisabledException(override val cause: Throwable? = null) :
    GlobalException(GlobalErrorCode.USER_DISABLED, cause = cause) {
    companion object {
        private const val serialVersionUID: Long = -2453178744314717271L
    }
}

class UsernameOrPasswordWrongException(override val cause: Throwable? = null) :
    GlobalException(GlobalErrorCode.USERNAME_PASSWORD_WRONG, cause = cause) {
    companion object {
        private const val serialVersionUID: Long = 717238600410775125L
    }
}

class UserHasBeBlockedException(override val cause: Throwable? = null) :
    GlobalException(GlobalErrorCode.USER_HAS_BE_BLOCKED, cause = cause) {
}