package com.kairlec.koj.util.crypto

/**
 * @author : Kairlec
 * @since : 2022/1/10
 **/
interface AsymmetricCrypto {
    val publicKey: String
    val privateKey: String
    fun encodeByPublic(raw: String): String
    fun encodeByPrivate(raw: String): String
    fun decodeByPublic(encoded: String): String
    fun decodeByPrivate(encoded: String): String
    fun encodeByPublic(raw: ByteArray): ByteArray
    fun encodeByPrivate(raw: ByteArray): ByteArray
    fun decodeByPublic(encoded: ByteArray): ByteArray
    fun decodeByPrivate(encoded: ByteArray): ByteArray
    fun sign(data: String): String
    fun sign(data: ByteArray): ByteArray
    fun verifySign(data: String, signature: String): Boolean
    fun verifySign(data: ByteArray, signature: ByteArray): Boolean
}
