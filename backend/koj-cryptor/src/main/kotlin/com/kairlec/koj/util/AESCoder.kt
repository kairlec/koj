package com.kairlec.koj.util

import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec


/**
 * @author : Kairlec
 * @since : 2022/1/10
 **/
interface AESCoder {
    fun encrypt(raw: ByteArray, key: SecretKey, iv: IvParameterSpec): ByteArray

    fun decrypt(encoded: ByteArray, key: SecretKey, iv: IvParameterSpec): ByteArray

    companion object : AESCoder by AESCoderImpl("AES/CBC/PKCS5Padding") {
        /**
         * 生成指定[n]长度的AES密钥
         */
        @JvmStatic
        fun generateKey(n: Int): SecretKey {
            val keyGenerator = KeyGenerator.getInstance("AES")
            keyGenerator.init(n)
            return keyGenerator.generateKey()
        }

        /**
         * 根据指定的密码与盐值,使用PBKDF2-SHA256算法生成AES密钥
         * 迭代次数65536次,长度为256位
         */
        @JvmStatic
        fun generateKeyFromPassword(password: String, salt: ByteArray): SecretKey {
            val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
            val spec = PBEKeySpec(password.toCharArray(), salt, 65536, 256)
            return SecretKeySpec(factory.generateSecret(spec).encoded, "AES")
        }

        /**
         * 生成iv
         */
        @JvmStatic
        fun generateIv(): IvParameterSpec {
            val iv = ByteArray(16)
            SecureRandom().nextBytes(iv)
            return IvParameterSpec(iv)
        }
    }
}

internal class AESCoderImpl(private val algorithm: String) : AESCoder {
    override fun encrypt(raw: ByteArray, key: SecretKey, iv: IvParameterSpec): ByteArray {
        val cipher = Cipher.getInstance(algorithm)
        cipher.init(Cipher.ENCRYPT_MODE, key, iv)
        return cipher.doFinal(raw)
    }

    override fun decrypt(encoded: ByteArray, key: SecretKey, iv: IvParameterSpec): ByteArray {
        val cipher = Cipher.getInstance(algorithm)
        cipher.init(Cipher.DECRYPT_MODE, key, iv)
        return cipher.doFinal(encoded)
    }
}
