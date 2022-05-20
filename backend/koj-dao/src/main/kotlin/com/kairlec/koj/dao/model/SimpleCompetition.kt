package com.kairlec.koj.dao.model

import java.time.LocalDateTime

data class SimpleCompetition(
    val id: Long,
    val name: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val joined: Boolean,
    val pwd: String?
)