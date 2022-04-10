package com.kairlec.koj.dao.model

import java.time.LocalDateTime

data class UserStat(
    val id: Long,
    val username: String,
    val submitted: Int,
    val ac: List<Long>,
    val createTime: LocalDateTime
)