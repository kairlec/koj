package com.kairlec.koj.common

import java.io.Closeable
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.*

class TempDirectory private constructor(
    private val path: Path
) : Closeable, Path by path {
    override fun close() {
        path.toFile().deleteRecursively()
    }

    fun createFileWithContent(filename: String, content: String): Path {
        return path.resolve(filename).apply {
            if (this.notExists()) {
                this.createFile()
            }
            writeText(content)
        }
    }

    fun createFileWithContent(filename: String, content: ByteArray): Path {
        return path.resolve(filename).apply {
            if (this.notExists()) {
                this.createFile()
            }
            writeBytes(content)
        }
    }

    companion object {
        private val tempDir = Path.of(System.getProperty("java.io.tmpdir"))
        fun createOrUse(
            id: String,
            prefix: String = "koj-",
        ): TempDirectory {
            val path = Files.newDirectoryStream(tempDir, "$prefix${id}-*").use {
                it.firstOrNull()
            } ?: createTempDirectory(
                "$prefix${id}-"
            )
            return TempDirectory(path)
        }
    }
}