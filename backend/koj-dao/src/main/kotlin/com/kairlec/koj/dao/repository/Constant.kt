@file:Suppress("NOTHING_TO_INLINE")

package com.kairlec.koj.dao.repository

import kotlinx.coroutines.flow.Flow
import org.jooq.Field
import org.jooq.Record
import org.jooq.UpdateSetFirstStep
import org.jooq.UpdateSetMoreStep
import org.jooq.impl.DSL
import reactor.core.publisher.Flux

data class PageData<T>(
    val data: Flow<T>,
    val total: Int,
)

data class FluxPageData<T>(
    val data: Flux<T>,
    val total: Int,
)

infix fun <T> Flow<T>.pg(total: Int): PageData<T> = PageData(this, total)

inline fun <T : Any> UpdateSetFirstStep<out Record>.setIfNotNull(
    field: Field<T>,
    value: T?
): UpdateSetMoreStep<out Record> {
    return set(field, DSL.nvl(value, field))
}

inline fun <T : Any> UpdateSetMoreStep<out Record>.setIfNotNull(
    field: Field<T>,
    value: T?
): UpdateSetMoreStep<out Record> {
    return set(field, DSL.nvl(value, field))
}