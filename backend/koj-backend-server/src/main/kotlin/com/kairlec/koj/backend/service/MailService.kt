package com.kairlec.koj.backend.service

import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

interface MailService {
    suspend fun sendMail(
        address: String,
        subject: String,
        body: String,
        mailType: MailType
    )
}

interface TimeLimitMail {
    fun getLimitKey(address: String, type: MailType): String {
        return "MAIL:LIMIT:${type.typename}:${address}"
    }

    val limitTime: Duration
}

sealed interface MailType {
    val typename: String
}

object ResetPasswordMail : MailType, TimeLimitMail {
    override val typename: String
        get() = "RESET_PASSWORD"
    override val limitTime: Duration
        get() = 1.minutes
}

