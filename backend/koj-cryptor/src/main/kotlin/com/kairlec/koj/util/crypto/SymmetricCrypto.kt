package com.kairlec.koj.util.crypto

/**
 * @author : Kairlec
 * @since : 2022/1/10
 **/
interface SymmetricCrypto {
    val key: String
    fun encode(raw: String): String
    fun encode(raw: ByteArray): ByteArray
    fun decode(encoded: String): String
    fun decode(encoded: ByteArray): ByteArray
}