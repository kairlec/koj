package com.kairlec.koj.backend.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "pulsar")
data class PulsarAdminProperties(
    val readTimeoutSec: Int = 15,
    val requestTimeoutSec: Int = 15,
    val autoCertRefreshTimeSec: Int = 60,
    val sslProvider: String? = null,
    val adminUrl: String? = null,
)
