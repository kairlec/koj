@file:Suppress("NOTHING_TO_INLINE")

package com.kairlec.koj.backend.util

import com.kairlec.koj.backend.exp.DataNotFoundException
import com.kairlec.koj.backend.exp.DataNotModifiedException
import com.kairlec.koj.dao.extended.ListCondition
import com.kairlec.koj.dao.repository.PageData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono


suspend inline fun currentRequest(): ServerHttpRequest {
    return Mono.deferContextual {
        Mono.just(it)
    }
        .awaitSingle()
        .get(ServerWebExchange::class.java)
        .request
}

suspend inline fun currentResponse(): ServerHttpResponse {
    return Mono.deferContextual {
        Mono.just(it)
    }
        .awaitSingle()
        .get(ServerWebExchange::class.java)
        .response
}

suspend inline fun currentServerWebExchange(): ServerWebExchange {
    return Mono.deferContextual {
        Mono.just(it)
    }
        .awaitSingle()
        .get(ServerWebExchange::class.java)
}

suspend inline fun currentListCondition(): ListCondition {
    val query = currentRequest().queryParams
    val sorts = query.getFirst("sort")?.split(",") ?: emptyList()
    val limit = query.getFirst("limit")?.toIntOrNull() ?: 10
    val seek = query.getFirst("seek")?.split(",") ?: emptyList()
    val search = query.getFirst("search")
    return ListCondition(
        sorts, limit, seek, search
    )
}

fun <T> PageData<T>.re(): RE<Flow<T>> {
    return data.ok {
        xCount(total)
    }
}

fun <T, R> PageData<T>.re(transform: suspend (value: T) -> R): RE<Flow<R>> {
    return data.map(transform).ok {
        xCount(total)
    }
}

inline fun <T : Any> T?.sureFound(message: String = "data not found", cause: Throwable? = null): T =
    this ?: throw DataNotFoundException(message, cause = cause)

inline fun <T : Any> T?.sureEffect(message: String = "data not modified", cause: Throwable? = null): T =
    this ?: throw DataNotModifiedException(message, cause = cause)
inline fun Boolean.sureEffect(message: String = "data not modified", cause: Throwable? = null): Boolean =
    if(this) true else throw DataNotModifiedException(message, cause = cause)