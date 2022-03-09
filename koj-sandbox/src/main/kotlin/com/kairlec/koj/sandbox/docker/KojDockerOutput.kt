package com.kairlec.koj.sandbox.docker

import java.nio.file.Path
import java.util.*

enum class Result(val value: Int) {
    NO_ERROR(0),
    CPU_TIME_LIMIT_EXCEEDED(1),
    REAL_TIME_LIMIT_EXCEEDED(2),
    MEMORY_LIMIT_EXCEEDED(3),
    RUNTIME_ERROR(4),
    SYSTEM_ERROR(5),
    OUTPUT_LIMIT_EXCEEDED(6),
    ;

    fun isError(): Boolean {
        return value != 0
    }

    companion object {
        fun from(value: Int): Result {
            return values().first { it.value == value }
        }
    }
}

enum class Error(val value: Int) {
    NO_ERROR(0),
    FORK_ERROR(-1),
    PTHREAD_ERROR(-2),
    WAIT_ERROR(-3),
    ;

    fun isError(): Boolean {
        return value != 0
    }

    companion object {
        fun from(value: Int): Error {
            return values().first { it.value == value }
        }
    }
}

data class Status(
    val cpuTime: Int,
    val real_time: Int,
    val memory: Long,
    val signal: Int,
    val exitCode: Int,
    val error: Error,
    val result: Result,
    val outputSize: Long,
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
                        Error.from(properties.getProperty("ERROR").toInt()),
                        Result.from(properties.getProperty("RESULT").toInt()),
                        properties.getProperty("OUTPUT_SIZE").toLong(),
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

data class KojDockerOutput(
    val containerCode: Int,
    val status: Status?,
    val stdout: Stdout?,
    val logging: Logging?,
) {
    fun exitCode(): Int {
        return if (containerCode == 0) {
            status?.exitCode ?: -1
        } else {
            containerCode
        }
    }

    fun isError(): Boolean {
        return exitCode() != 0 || status?.error?.isError() == true || status?.result?.isError() == true
    }
}