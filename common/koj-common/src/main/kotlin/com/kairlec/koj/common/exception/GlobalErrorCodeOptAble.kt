package com.kairlec.koj.common.exception

/**
 * @author : Kairlec
 * @since : 2022/2/14
 **/
interface GlobalErrorCodeOptAble {
    val errorCode: GlobalErrorCode

    /**
     * 相应错误码下,附带的额外数据
     */
    val extendData: Any? get() = null
}