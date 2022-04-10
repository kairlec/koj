@file:JvmName("Cryptor")

package com.kairlec.koj.util

import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.util.io.pem.PemObject
import org.bouncycastle.util.io.pem.PemWriter
import java.io.StringWriter
import java.security.KeyPair
import java.security.Security
import java.security.interfaces.RSAKey
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.util.*
import javax.crypto.spec.IvParameterSpec

/**
 * @author : Kairlec
 * @since : 2022/1/10
 **/
private object Constant {
    init {
        Security.addProvider(BouncyCastleProvider())
    }
}

operator fun KeyPair.component1(): RSAPublicKey {
    return this.public as RSAPublicKey
}

operator fun KeyPair.component2(): RSAPrivateKey {
    return this.private as RSAPrivateKey
}

fun ByteArray.base64(): String {
    return Base64.getEncoder().encodeToString(this)
}

fun ByteArray.base64UrlSafe(): String {
    return Base64.getUrlEncoder().encodeToString(this)
}

fun String.base64(): ByteArray {
    return Base64.getDecoder().decode(this)
}

fun String.base64UrlSafe(): ByteArray {
    return Base64.getUrlDecoder().decode(this)
}

fun String.base62(): ByteArray {
    return Base62.decode(this.toByteArray())
}

fun ByteArray.base62(): String {
    return Base62.encode(this).toString(Charsets.UTF_8)
}

fun RSAKey.toPemString(): String {
    val stringWriter = StringWriter()
    val pemObject = when (this) {
        is RSAPrivateKey -> PemObject("PRIVATE KEY", encoded)
        is RSAPublicKey -> PemObject("PUBLIC KEY", encoded)
        else -> throw IllegalStateException("not support rsa key type")
    }
    val pemWriter = PemWriter(stringWriter)
    pemWriter.writeObject(pemObject)
    pemWriter.flush()
    return stringWriter.toString()
}

fun IvParameterSpec.toBase64String(): String {
    return iv.base64()
}

fun String.iv(): IvParameterSpec {
    return IvParameterSpec(base64())
}

fun String.urlSafe(): String {
    return this.replace('+', '-').replace('/', '_')
}

fun String.unUrlSafe(): String {
    return this.replace('-', '+').replace('_', '/')
}
