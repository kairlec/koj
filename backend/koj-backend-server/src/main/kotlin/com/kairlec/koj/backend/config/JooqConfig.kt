package com.kairlec.koj.backend.config

import com.kairlec.koj.dao.DSLAccess
import io.r2dbc.spi.Connection
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.mono
import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.conf.Settings
import org.jooq.impl.DSL
import org.reactivestreams.Publisher
import org.springframework.boot.autoconfigure.jooq.JooqProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.r2dbc.core.DatabaseClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux

@Configuration
@EnableConfigurationProperties(JooqProperties::class)
class JooqConfig(
    private val jooqProperties: JooqProperties,
) {
    @Bean
    fun dslAccess(databaseClient: DatabaseClient) =
        DSLAccessImpl(databaseClient, jooqProperties.sqlDialect ?: SQLDialect.MYSQL)
}

class DSLAccessImpl(
    private val databaseClient: DatabaseClient,
    private val sqlDialect: SQLDialect
) : DSLAccess {
    private val settings = Settings()
        .withBindOffsetDateTimeType(true)
        .withBindOffsetTimeType(true)

    private fun Connection.dsl() =
        DSL.using(this, sqlDialect, settings)

    override fun <T : Any> withDSLContextMany(block: (DSLContext) -> Publisher<T>): Flux<T> =
        databaseClient.inConnectionMany { con -> block(con.dsl()).toFlux() }

    override fun <T : Any> withDSLContextMono(block: (DSLContext) -> Mono<T>): Mono<T> =
        databaseClient.inConnection { con -> block(con.dsl()) }

    override suspend fun <T : Any> withDSLContext(block: suspend (DSLContext) -> T): T =
        databaseClient.inConnection { con -> mono { block(con.dsl()) } }.awaitSingle()
}
