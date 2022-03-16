package com.kairlec.koj.common

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.util.zip.Deflater
import java.util.zip.DeflaterOutputStream
import java.util.zip.Inflater
import java.util.zip.InflaterInputStream

fun ByteArray.compress(
    def: Deflater = Deflater(),
    size: Int = 512,
    syncFlush: Boolean = true
): ByteArray {
    ByteArrayOutputStream().use { baos ->
        baos.compress(def, size, syncFlush).use { dos ->
            dos.write(this)
        }
        return baos.toByteArray()
    }
}

fun ByteArray.decompress(
    inf: Inflater = Inflater(),
    size: Int = 512,
): ByteArray {
    ByteArrayInputStream(this).use { bais ->
        bais.decompress(inf, size).use { dis ->
            return dis.readAllBytes()
        }
    }
}

fun InputStream.decompress(
    inf: Inflater = Inflater(),
    size: Int = 512,
): InputStream {
    return InflaterInputStream(this, inf, size)
}

fun OutputStream.compress(
    def: Deflater = Deflater(),
    size: Int = 512,
    syncFlush: Boolean = true
): OutputStream {
    return DeflaterOutputStream(this, def, size, syncFlush)
}