package com.kairlec.koj.dao.model

import java.time.LocalDateTime

inline fun <reified T> parse(value: Byte): T where T : Enum<T>, T : ValueAble {
    return enumValues<T>().first { it.value == value }
}

data class SubmitDetail(
    val id: Long,
    val problemId: Long,
    val state: SubmitState,
    val castMemory: Long?,
    val castTime: Long?,
    val languageId: String,
    val belongUserId: Long,
    val username: String,
    val createTime: LocalDateTime,
    val updateTime: LocalDateTime,
    val code: String,
    val stderr: String?
)

data class SimpleSubmit(
    val id: Long,
    val problemId: Long,
    val state: SubmitState,
    val castMemory: Long?,
    val castTime: Long?,
    val languageId: String,
    val belongUserId: Long,
    val username: String,
    val createTime: LocalDateTime,
    val updateTime: LocalDateTime,
)

sealed interface ValueAble {
    val value: Byte
}

enum class SubmitState(override val value: Byte, val lessThan: Byte) : ValueAble {
    // 任务状态
    IN_QUEUE(0, 0),
    IN_COMPILING(1, 1),
    IN_RUNNING(2, 2),
    IN_JUDGING(13, 3),

    //结果
    WRONG_ANSWER(3, Byte.MAX_VALUE),
    ACCEPTED(4, Byte.MAX_VALUE),
    TIME_LIMIT_EXCEEDED(5, Byte.MAX_VALUE),
    MEMORY_LIMIT_EXCEEDED(6, Byte.MAX_VALUE),
    OUTPUT_LIMIT_EXCEEDED(7, Byte.MAX_VALUE),
    RUNTIME_ERROR(9, Byte.MAX_VALUE),
    SYSTEM_ERROR(10, Byte.MAX_VALUE),
    COMPILATION_ERROR(11, Byte.MAX_VALUE),
    PRESENTATION_ERROR(12, Byte.MAX_VALUE),

    // 其他
    UNKNOWN(13, Byte.MAX_VALUE),

    //没有可用的沙盒接收任务
    NO_SANDBOX(14, Byte.MAX_VALUE),
    ;

    companion object {
        fun parse(value: Byte) = parse<SubmitState>(value)
    }
}