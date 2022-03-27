package com.kairlec.koj.dao

import org.jooq.DSLContext
import org.reactivestreams.Publisher
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface DSLAccess {
    fun <T : Any> withDSLContextMany(block: (DSLContext) -> Publisher<T>): Flux<T>

    fun <T : Any> withDSLContextMono(block: (DSLContext) -> Mono<T>): Mono<T>

    suspend fun <T : Any> withDSLContext(block: suspend (DSLContext) -> T): T
}