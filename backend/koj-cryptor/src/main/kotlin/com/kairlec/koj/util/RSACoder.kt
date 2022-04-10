package com.kairlec.koj.util

import org.bouncycastle.util.io.pem.PemReader
import java.io.StringReader
import java.security.*
import java.security.interfaces.RSAKey
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher


/**
 * @author : Kairlec
 * @since : 2021/12/27
 **/

interface RSACoder {
    /**
     * 用密钥对信息生成数字签名
     *
     * @param data 加密数据
     * @param privateKey  密钥
     * @return
     */
    fun sign(
        data: ByteArray,
        privateKey: RSAPrivateKey,
    ): ByteArray

    /**
     * 校验数字签名
     *
     * @param data  加密数据
     * @param publicKey   密钥
     * @param sign  数字签名
     * @return 校验成功返回true 失败返回false
     */
    fun verify(
        data: ByteArray,
        publicKey: RSAPublicKey,
        sign: ByteArray,
    ): Boolean

    /**
     * 加密
     * @param data 要加密的数据
     * @param key 要加密的密钥
     */
    fun encrypt(
        data: ByteArray,
        key: RSAKey
    ): ByteArray

    /**
     * 解密
     * @param encrypted 加密后的数据
     * @param key 要解密的密钥
     */
    fun decrypt(
        encrypted: ByteArray,
        key: RSAKey
    ): ByteArray

    /**
     * 生成密钥对
     */
    fun generatorKeyPair(keySize: Int = 2048, random: SecureRandom = SecureRandom.getInstance("SHA1PRNG")): KeyPair

    /**
     * 从pem读取私钥
     */
    fun fromPrivatePem(pem: String): RSAPrivateKey

    /**
     * 从pem读取公钥
     */
    fun fromPublicPem(pem: String): RSAPublicKey

    companion object : RSACoder by RSACoderImpl()
}

internal class RSACoderImpl : RSACoder {
    companion object {
        private const val KEY_ALGORITHM = "RSA"
        private const val CIPHER_ALGORITHM = "RSA/ECB/PKCS1Padding"
        private const val SIGNATURE_ALGORITHM = "SHA256withRSA"
        private val keyFactory by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { KeyFactory.getInstance(KEY_ALGORITHM) }
    }

    override fun sign(
        data: ByteArray,
        privateKey: RSAPrivateKey,
    ): ByteArray {
        val signature = Signature.getInstance(SIGNATURE_ALGORITHM)
        signature.initSign(privateKey)
        signature.update(data)
        return signature.sign()
    }

    override fun verify(
        data: ByteArray,
        publicKey: RSAPublicKey,
        sign: ByteArray,
    ): Boolean {
        val signature = Signature.getInstance(SIGNATURE_ALGORITHM)
        signature.initVerify(publicKey)
        signature.update(data)
        return signature.verify(sign)
    }

    override fun encrypt(
        data: ByteArray,
        key: RSAKey
    ): ByteArray {
        val cipher = Cipher.getInstance(CIPHER_ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, key as Key)
        return cipher.doFinal(data)
    }

    override fun decrypt(
        encrypted: ByteArray,
        key: RSAKey
    ): ByteArray {
        val cipher = Cipher.getInstance(CIPHER_ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, key as Key)
        return cipher.doFinal(encrypted)
    }

    override fun generatorKeyPair(keySize: Int, random: SecureRandom): KeyPair {
        val keyGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM)
        keyGenerator.initialize(keySize, random)
        return keyGenerator.generateKeyPair()
    }

    override fun fromPrivatePem(pem: String): RSAPrivateKey {
        return keyFactory.generatePrivate(PKCS8EncodedKeySpec(PemReader(StringReader(pem)).readPemObject().content)) as RSAPrivateKey
    }

    override fun fromPublicPem(pem: String): RSAPublicKey {
        return keyFactory.generatePublic(X509EncodedKeySpec(PemReader(StringReader(pem)).readPemObject().content)) as RSAPublicKey
    }
}
