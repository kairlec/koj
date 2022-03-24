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

fun <R : Record> SelectWhereStep<R>.list(
    field: TableField<R, Long>,
    listCondition: ListCondition
): SelectForUpdateStep<R> {
    val table = field.table!!
    return where(table.search(listCondition.search))
        .sort(table, listCondition.sorts)
        .seekLimit(listCondition.limit, listCondition.seek)
}

fun <R : Record> SelectWhereStep<R>.listUnsafe(
    table: Table<*>,
    field: Field<Long>,
    listCondition: ListCondition
): SelectForUpdateStep<R> {
    return where(table.search(listCondition.search))
        .sortUnsafe(table, listCondition.sorts)
        .seekLimit(listCondition.limit, listCondition.seek)
}

fun <R : Record> SelectConditionStep<R>.list(
    field: TableField<R, Long>,
    listCondition: ListCondition
): SelectForUpdateStep<R> {
    val table = field.table!!
    return and(table.search(listCondition.search))
        .sort(table, listCondition.sorts)
        .seekLimit(listCondition.limit, listCondition.seek)
}

fun <R : Record> SelectConditionStep<R>.listUnsafe(
    table: Table<*>,
    field: Field<Long>,
    listCondition: ListCondition
): SelectForUpdateStep<R> {
    return and(table.search(listCondition.search))
        .sortUnsafe(table, listCondition.sorts)
        .seekLimit(listCondition.limit, listCondition.seek)
}

fun <R : Record> SelectConditionStep<R>.listFinal(
    field: TableField<R, Long>,
    listCondition: ListCondition?
): ResultQuery<R> {
    return if (listCondition == null) {
        this
    } else {
        list(field, listCondition)
    }
}

fun <R : Record> SelectConditionStep<R>.listFinalUnsafe(
    table: Table<*>,
    field: Field<Long>,
    listCondition: ListCondition?
): ResultQuery<R> {
    return if (listCondition == null) {
        this
    } else {
        listUnsafe(table, field, listCondition)
    }
}