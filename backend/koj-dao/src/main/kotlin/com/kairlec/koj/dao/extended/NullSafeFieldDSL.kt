package com.kairlec.koj.dao.extended

import org.jooq.*
import org.jooq.impl.DSL

/**
 * @author : Kairlec
 * @since : 2022/3/1
 **/

data class NullSafeFieldDSL<R : Record>(
    internal val map: MutableMap<TableField<R, *>, Any?> = mutableMapOf()
) {
    operator fun <T> set(field: TableField<R, T>, value: T?) {
        map[field] = value
    }

    operator fun <T> set(field: TableField<R, T>, value: Field<T>) {
        map[field] = value
    }

    operator fun <T> set(field: TableField<R, T>, value: Default<T>) {
        if (value.data != null) {
            map[field] = value.data
        } else {
            map[field] = DSL.defaultValue(field)
        }
    }

    class Default<T>(val data: T?)

    val <T> T?.orDefault: Default<T>
        get() {
            return Default(this)
        }
}

fun <R : Record> InsertSetStep<R>.value(block: NullSafeFieldDSL<R>.() -> Unit): InsertValuesStepN<R> {
    val dsl = NullSafeFieldDSL<R>()
    dsl.block()
    val columns = dsl.map.keys
    val values = dsl.map.values
    return this.columns(columns)
        .values(values)
}

fun <R : Record> DSLContext.insertInto(
    table: Table<R>,
    block: NullSafeFieldDSL<R>.() -> Unit
): InsertValuesStepN<R> {
    return insertInto(table).value(block)
}