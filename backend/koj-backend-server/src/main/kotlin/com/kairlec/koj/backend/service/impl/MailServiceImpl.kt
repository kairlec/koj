package com.kairlec.koj.backend.service.impl

import com.kairlec.koj.backend.exp.MailSendTimeLimitException
import com.kairlec.koj.backend.service.MailService
import com.kairlec.koj.backend.service.MailType
import com.kairlec.koj.backend.service.TimeLimitMail
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.data.redis.core.ReactiveRedisOperations
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import kotlin.time.toJavaDuration
import kotlin.time.toKotlinDuration

@Service
class MailServiceImpl(
    private val javaMailSender: JavaMailSender,
    private val redisOperations: ReactiveRedisOperations<String, String>,
) : MailService {
    private suspend fun send(address: String, subject: String, body: String) {
        val mail = javaMailSender.createMimeMessage().also {
            val helper = MimeMessageHelper(it)
            helper.setTo(address)
            helper.setSubject(subject)
            helper.setText(body)
        }
        javaMailSender.send(mail)
    }

    override suspend fun sendMail(
        address: String,
        subject: String,
        body: String,
        mailType: MailType
    ) {
        if (mailType is TimeLimitMail) {
            val key = mailType.getLimitKey(address, mailType)
            if (redisOperations.hasKey(key).awaitSingle()) {
                val leftTime = redisOperations.getExpire(key).awaitSingle()
                throw MailSendTimeLimitException(leftTime.toKotlinDuration())
            }
            val limitTime = mailType.limitTime
            send(address, subject, body)
            redisOperations.opsForValue().set(key, "WAITING", limitTime.toJavaDuration()).awaitSingle()
        } else {
            send(address, subject, body)
        }
    }

}