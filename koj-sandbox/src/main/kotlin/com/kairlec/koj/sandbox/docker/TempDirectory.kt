package com.kairlec.koj.sandbox.docker

import java.io.Closeable
import java.io.File
import java.nio.file.Path
import kotlin.io.path.createTempDirectory
import kotlin.io.path.deleteIfExists

class TempDirectory private constructor(
    private val path: Path
) : Closeable, File(path.toUri()) {
    override fun close() {
        path.deleteIfExists()
    }

    companion object {
        fun create(
            id: String,
            prefix: String = "koj-",
        ): com.kairlec.koj.sandbox.docker.TempDirectory {
            val path = createTempDirectory(
                "$prefix$id-"
            )
            return TempDirectory(path)
        }
    }
}