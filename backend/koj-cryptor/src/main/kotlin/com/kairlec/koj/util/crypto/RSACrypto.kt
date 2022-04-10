package com.kairlec.koj.util.crypto

import com.kairlec.koj.util.*
import java.security.KeyPair
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey

/**
 * @author : Kairlec
 * @since : 2022/1/10
 **/
class RSACrypto(keyPair: KeyPair) : AsymmetricCrypto {
    override val publicKey: String
    override val privateKey: String
    private val rsaPublicKey: RSAPublicKey
    private val rsaPrivateKey: RSAPrivateKey

    init {
        rsaPublicKey = keyPair.component1()
        rsaPrivateKey = keyPair.component2()
        publicKey = rsaPublicKey.toPemString()
        privateKey = rsaPrivateKey.toPemString()
    }

    override fun encodeByPublic(raw: String): String {
        return RSACoder.encrypt(raw.toByteArray(), rsaPublicKey).base64UrlSafe()
    }

    override fun encodeByPrivate(raw: String): String {
        return RSACoder.encrypt(raw.toByteArray(), rsaPrivateKey).base64UrlSafe()
    }

    override fun decodeByPublic(encoded: String): String {
        return String(RSACoder.decrypt(encoded.base64UrlSafe(), rsaPublicKey))
    }

    override fun decodeByPrivate(encoded: String): String {
        return String(RSACoder.decrypt(encoded.base64UrlSafe(), rsaPrivateKey))
    }

    override fun encodeByPublic(raw: ByteArray): ByteArray {
        return RSACoder.encrypt(raw, rsaPublicKey)
    }

    override fun encodeByPrivate(raw: ByteArray): ByteArray {
        return RSACoder.encrypt(raw, rsaPrivateKey)
    }

    override fun decodeByPublic(encoded: ByteArray): ByteArray {
        return RSACoder.decrypt(encoded, rsaPublicKey)
    }

    override fun decodeByPrivate(encoded: ByteArray): ByteArray {
        return RSACoder.decrypt(encoded, rsaPrivateKey)
    }

    override fun sign(data: String): String {
        return RSACoder.sign(data.toByteArray(), rsaPrivateKey).base64UrlSafe()
    }

    override fun sign(data: ByteArray): ByteArray {
        return RSACoder.sign(data, rsaPrivateKey)
    }

    override fun verifySign(data: String, signature: String): Boolean {
        return RSACoder.verify(data.toByteArray(), rsaPublicKey, signature.base64UrlSafe())
    }

    override fun verifySign(data: ByteArray, signature: ByteArray): Boolean {
        return RSACoder.verify(data, rsaPublicKey, signature)
    }

    override fun toString(): String {
        return "$publicKey;$privateKey"
    }

    companion object {
        @JvmStatic
        fun of(str: String): RSACrypto? {
            val split = str.split(";").toTypedArray()
            if (split.size != 2) {
                return null
            }
            val rsaPublicKey = RSACoder.fromPublicPem(split[0])
            val rsaPrivateKey = RSACoder.fromPrivatePem(split[1])
            return RSACrypto(KeyPair(rsaPublicKey, rsaPrivateKey))
        }
    }
}
