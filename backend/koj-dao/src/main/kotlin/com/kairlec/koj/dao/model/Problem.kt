package com.kairlec.koj.dao.model

import java.time.LocalDateTime

data class SimpleProblem(
    val id: Long,
    val name: String,
    val spj: Boolean,
    val idx: Byte?,
    val tags: List<String>
)

data class ProblemConfig(
    val languageId: String,
    val memoryLimit: Int,
    val timeLimit: Int,
    val createTime: LocalDateTime,
    val updateTime: LocalDateTime
)

data class Problem(
    val id: Long,
    val name: String,
    val content: String,
    val spj: Boolean,
    val createTime: LocalDateTime,
    val updateTime: LocalDateTime,
    val config: List<ProblemConfig>,
    val idx: Byte?,
    val tags: List<String>
)