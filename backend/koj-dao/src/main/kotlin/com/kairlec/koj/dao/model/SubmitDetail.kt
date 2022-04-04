package com.kairlec.koj.dao.model

import java.time.LocalDateTime

inline fun <reified T> parse(value: Byte): T where T : Enum<T>, T : ValueAble {
    return enumValues<T>().first { it.value == value }
}

data class SubmitDetail(
    val id: Long,
    val state: SubmitState,
    val castMemory: Int?,
    val castTime: Int?,
    val languageId: String,
    val belongCompetitionId: Long,
    val belongUserId: Long,
    val createTime: LocalDateTime,
    val updateTime: LocalDateTime,
    val code: String,
)

sealed interface ValueAble {
    val value: Byte
}

enum class SubmitState(override val value: Byte, val lessThan: Byte) : ValueAble {
    // 任务状态
    IN_QUEUE(0, 0),
    IN_COMPILING(1, 1),
    IN_RUNNING(2, 2),

    //结果
    WRONG_ANSWER(3, 3),
    ACCEPTED(4, 3),
    TIME_LIMIT_EXCEEDED(5, 3),
    MEMORY_LIMIT_EXCEEDED(6, 3),
    OUTPUT_LIMIT_EXCEEDED(7, 3),
    TIME_LIMIT_EXECUTED(8, 3),
    RUNTIME_ERROR(9, 3),
    SYSTEM_ERROR(10, 3),
    COMPILATION_ERROR(11, 3),
    PRESENTATION_ERROR(12, 3),

    // 其他
    UNKNOWN(13, 3),

    //没有可用的沙盒接收任务
    NO_SANDBOX(14, 3),
    ;

    companion object {
        fun parse(value: Byte) = parse<SubmitState>(value)
    }
}