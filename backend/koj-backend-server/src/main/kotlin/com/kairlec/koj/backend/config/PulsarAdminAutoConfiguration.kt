package com.kairlec.koj.backend.config

import com.google.common.base.Strings
import com.kairlec.koj.backend.config.properties.PulsarAdminProperties
import io.github.majusko.pulsar.error.exception.ClientInitException
import io.github.majusko.pulsar.properties.PulsarProperties
import org.apache.pulsar.client.admin.PulsarAdmin
import org.apache.pulsar.client.api.AuthenticationFactory
import org.apache.pulsar.client.impl.auth.oauth2.AuthenticationFactoryOAuth2
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import java.net.URI
import java.net.URL
import java.util.concurrent.TimeUnit

@Configuration
@ComponentScan
@EnableConfigurationProperties(PulsarProperties::class, PulsarAdminProperties::class)
class PulsarAdminAutoConfiguration(
    private val pulsarProperties: PulsarProperties,
    private val pulsarAdminProperties: PulsarAdminProperties
) {
    @Bean
    @ConditionalOnMissingBean
    fun pulsarAdmin(): PulsarAdmin {
        if (!Strings.isNullOrEmpty(pulsarProperties.tlsAuthCertFilePath) &&
            !Strings.isNullOrEmpty(pulsarProperties.tlsAuthKeyFilePath) &&
            !Strings.isNullOrEmpty(pulsarProperties.tokenAuthValue)
        ) throw ClientInitException("You cannot use multiple auth options.")
        val adminUrl = pulsarAdminProperties.adminUrl ?: pulsarProperties.serviceUrl.let {
            @Suppress("HttpUrlsUsage") "http://${URI.create(it).host}:8080"
        }
        val pulsarAdminBuilder = PulsarAdmin.builder()
            .serviceHttpUrl(adminUrl)
            .connectionTimeout(pulsarProperties.connectionTimeoutSec, TimeUnit.SECONDS)
            .useKeyStoreTls(pulsarProperties.isUseKeyStoreTls)
            .tlsTrustCertsFilePath(pulsarProperties.tlsTrustCertsFilePath)
            .tlsCiphers(pulsarProperties.tlsCiphers)
            .tlsProtocols(pulsarProperties.tlsProtocols)
            .tlsTrustStorePassword(pulsarProperties.tlsTrustStorePassword)
            .tlsTrustStorePath(pulsarProperties.tlsTrustStorePath)
            .tlsTrustStoreType(pulsarProperties.tlsTrustStoreType)
            .allowTlsInsecureConnection(pulsarProperties.isAllowTlsInsecureConnection)
            .enableTlsHostnameVerification(pulsarProperties.isEnableTlsHostnameVerification)
            .readTimeout(pulsarAdminProperties.readTimeoutSec, TimeUnit.SECONDS)
            .requestTimeout(pulsarAdminProperties.requestTimeoutSec, TimeUnit.SECONDS)
            .autoCertRefreshTime(pulsarAdminProperties.autoCertRefreshTimeSec, TimeUnit.SECONDS)
            .apply {
                if (pulsarAdminProperties.sslProvider != null) {
                    sslProvider(pulsarAdminProperties.sslProvider)
                }
            }
        if (!Strings.isNullOrEmpty(pulsarProperties.tlsAuthCertFilePath) &&
            !Strings.isNullOrEmpty(pulsarProperties.tlsAuthKeyFilePath)
        ) {
            pulsarAdminBuilder.authentication(
                AuthenticationFactory
                    .TLS(pulsarProperties.tlsAuthCertFilePath, pulsarProperties.tlsAuthKeyFilePath)
            )
        }
        if (!Strings.isNullOrEmpty(pulsarProperties.tokenAuthValue)) {
            pulsarAdminBuilder.authentication(
                AuthenticationFactory
                    .token(pulsarProperties.tokenAuthValue)
            )
        }
        if (!Strings.isNullOrEmpty(pulsarProperties.oauth2Audience) &&
            !Strings.isNullOrEmpty(pulsarProperties.oauth2IssuerUrl) &&
            !Strings.isNullOrEmpty(pulsarProperties.oauth2CredentialsUrl)
        ) {
            val issuerUrl = URL(pulsarProperties.oauth2IssuerUrl)
            val credentialsUrl = URL(pulsarProperties.oauth2CredentialsUrl)
            pulsarAdminBuilder.authentication(
                AuthenticationFactoryOAuth2
                    .clientCredentials(issuerUrl, credentialsUrl, pulsarProperties.oauth2Audience)
            )
        }
        return pulsarAdminBuilder.build()
    }
}