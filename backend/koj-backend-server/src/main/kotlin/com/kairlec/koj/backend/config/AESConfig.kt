package com.kairlec.koj.backend.config

import com.kairlec.koj.util.crypto.AESCrypto
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AESConfig {
    @Value("\${aes.password}")
    private lateinit var password: String

    @Value("\${aes.salt}")
    private lateinit var salt: String

    @Value("\${aes.iv}")
    private lateinit var iv: String

    @Bean
    fun aesCrypto(): AESCrypto {
        return AESCrypto(password, salt, iv)
    }
}