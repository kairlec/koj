package com.kairlec.koj.backend.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "pulsar")
data class PulsarAdminProperties(
    var readTimeoutSec: Int = 15,
    var requestTimeoutSec: Int = 15,
    var autoCertRefreshTimeSec: Int = 60,
    var sslProvider: String? = null,
    var adminUrl: String? = null,
)
