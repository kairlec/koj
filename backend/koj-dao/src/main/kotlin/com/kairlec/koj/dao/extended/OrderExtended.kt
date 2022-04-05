package com.kairlec.koj.dao.extended

import org.jooq.*

/**
 * @author : Kairlec
 * @since : 2022/3/1
 **/

private const val DESC_CHAR = '~'
private const val ASC_CHAR = '-'


fun <R : Record> SelectOrderByStep<R>.sort(table: Table<out Record>, vararg fields: String): SelectSeekStepN<R> {
    return sort(table, fields.toList())
}

fun <R : Record> FieldSet<R>.sortFields(fields: List<String>): List<SortField<out Any>> {
    return fields.filter { (it.startsWith(DESC_CHAR) || it.startsWith(ASC_CHAR)) && it.length > 1 }
        .mapNotNull { field ->
            val asc = field.startsWith(ASC_CHAR)
            val fieldName = field.substring(1)
            this[fieldName]?.let {
                if (asc) it.asc() else it.desc()
            }
        }
}

fun <R : Record> SelectOrderByStep<R>.sort(table: Table<out Record>, fields: List<String>): SelectSeekStepN<R> {
    return orderBy(table.fieldsSet().sortFields(fields))
}

fun <R : Record> SelectOrderByStep<R>.sort(fields: List<SortField<out Any>>): SelectSeekStepN<R> {
    return orderBy(fields)
}

fun <R : Record> SelectSeekStepN<R>.seekLimit(limit: Int, seek: List<Any>): SelectForUpdateStep<R> {
    require(limit > 0) { "limit must be greater than 0" }
    return if (seek.isNotEmpty()) {
        seek(*seek.toTypedArray()).limit(limit)
    } else {
        limit(limit)
    }
}

fun <R : Record> SelectSeekStepN<R>.seekLimit(limit: Int, page: Int): SelectForUpdateStep<R> {
    require(limit > 0) { "limit must be greater than 0" }
    require(page > 0) { "page must be greater than 0" }
    return limit(limit).offset((page - 1) * limit)
}