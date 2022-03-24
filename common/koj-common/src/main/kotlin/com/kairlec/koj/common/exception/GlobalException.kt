package com.kairlec.koj.common.exception

import mu.KotlinLogging

/**
 * @author : Kairlec
 * @since : 2022/2/14
 **/
abstract class GlobalException @JvmOverloads constructor(
    override val errorCode: GlobalErrorCode,
    override val message: String = errorCode.message,
    override val extendData: Any? = null,
    override val cause: Throwable? = null,
) : GlobalErrorCodeOptAble,
    RuntimeException(message, cause, false, (cause != null && cause !is GlobalException) || log.isDebugEnabled) {
    companion object {
        private val log = KotlinLogging.logger { }

        private const val serialVersionUID: Long = 1461712681838893336L
    }
}