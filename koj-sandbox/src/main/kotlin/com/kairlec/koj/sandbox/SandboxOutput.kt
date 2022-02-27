package com.kairlec.koj.sandbox

import java.nio.file.Path
import java.util.*

data class Status(
    val cpuTime: Int,
    val real_time: Int,
    val memory: Long,
    val signal: Int,
    val exitCode: Int,
    val error: Int,
    val result: Int,
    val wrong: String?
) {
    companion object {
        fun load(path: Path): Status {
            return path.toFile().inputStream().use {
                Properties().let { properties ->
                    properties.load(it)
                    Status(
                        properties.getProperty("CPU_TIME").toInt(),
                        properties.getProperty("REAL_TIME").toInt(),
                        properties.getProperty("MEMORY").toLong(),
                        properties.getProperty("SIGNAL").toInt(),
                        properties.getProperty("EXIT_CODE").toInt(),
                        properties.getProperty("ERROR").toInt(),
                        properties.getProperty("RESULT").toInt(),
                        properties.getProperty("WRONG")
                    )
                }
            }
        }
    }
}

@JvmInline
value class Stdout(val value: String)

@JvmInline
value class Logging(val value: String)

data class SandboxOutput(
    val status: Status,
    val stdout: Stdout?,
    val logging: Logging?,
)