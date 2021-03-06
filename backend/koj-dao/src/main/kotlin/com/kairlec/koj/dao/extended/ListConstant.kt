@file:Suppress("NOTHING_TO_INLINE")

package com.kairlec.koj.dao.extended

import org.jooq.*

/**
 * @author : Kairlec
 * @since : 2022/3/8
 **/

data class ListCondition(
    val sorts: List<String> = emptyList(),
    val limit: Int = 10,
    val seek: List<Any> = emptyList(),
    val search: String? = null,
)

inline fun <R : Record> SelectWhereStep<R>.list(
    table: Table<out Record>,
    listCondition: ListCondition
): SelectForUpdateStep<R> {
    return where(table.search(listCondition.search))
        .sort(table, listCondition.sorts)
        .seekLimit(listCondition.limit, listCondition.seek)
}

inline fun SelectWhereStep<Record1<Int>>.listCount(
    table: Table<out Record>,
    listCondition: ListCondition
): SelectForUpdateStep<Record1<Int>> {
    return where(table.search(listCondition.search))
}

inline fun <R : Record> SelectConditionStep<R>.list(
    table: Table<out Record>,
    listCondition: ListCondition
): SelectForUpdateStep<R> {
    return and(table.search(listCondition.search))
        .sort(table, listCondition.sorts)
        .seekLimit(listCondition.limit, listCondition.seek)
}

inline fun SelectConditionStep<Record1<Int>>.listCount(
    table: Table<out Record>,
    listCondition: ListCondition
): SelectForUpdateStep<Record1<Int>> {
    return and(table.search(listCondition.search))
}

inline fun <R : Record> SelectConditionStep<R>.listFinal(
    table: Table<out Record>,
    listCondition: ListCondition?
): ResultQuery<R> {
    return if (listCondition == null) {
        this
    } else {
        list(table, listCondition)
    }
}

inline fun SelectConditionStep<Record1<Int>>.listCountFinal(
    table: Table<out Record>,
    listCondition: ListCondition?
): ResultQuery<Record1<Int>> {
    return if (listCondition == null) {
        this
    } else {
        list(table, listCondition)
    }
}