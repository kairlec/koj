package com.kairlec.koj.util.crypto

import com.kairlec.koj.util.AESCoder
import com.kairlec.koj.util.base64
import com.kairlec.koj.util.iv

/**
 * @author : Kairlec
 * @since : 2022/1/10
 **/
@Suppress("CanBeParameter", "MemberVisibilityCanBePrivate", "unused")
class AESCrypto(
    val password: String,
    val salt: String,
    val iv: String
) : SymmetricCrypto {
    private val ivParameterSpec = iv.iv()
    private val secretKey = AESCoder.generateKeyFromPassword(password, salt.base64())
    override val key = secretKey.encoded.base64()

    override fun encode(raw: String): String {
        return AESCoder.encrypt(raw.toByteArray(), secretKey, ivParameterSpec).base64()
    }

    override fun encode(raw: ByteArray): ByteArray {
        return AESCoder.encrypt(raw, secretKey, ivParameterSpec)
    }

    override fun decode(encoded: String): String {
        return AESCoder.decrypt(encoded.base64(), secretKey, ivParameterSpec).toString(Charsets.UTF_8)
    }

    override fun decode(encoded: ByteArray): ByteArray {
        return AESCoder.decrypt(encoded, secretKey, ivParameterSpec)
    }
}