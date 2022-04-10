package com.kairlec.koj.dao

import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.jooq.DSLContext
import org.reactivestreams.Publisher
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

interface DSLAccess {
    fun <T : Any> flux(block: DSLContext.() -> Publisher<T>): Flux<T>

    fun <T> mono(block: DSLContext.() -> Mono<T>): Mono<T>
}

@OptIn(ExperimentalContracts::class)
suspend inline fun <T> DSLAccess.with(crossinline block: suspend DSLContext.() -> T): T {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return mono context@{
        kotlinx.coroutines.reactor.mono {
            block(this@context)
        }
    }.awaitFirstOrNull() as T
}
