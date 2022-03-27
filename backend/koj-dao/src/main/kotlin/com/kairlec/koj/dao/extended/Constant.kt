package com.kairlec.koj.dao.extended

import org.jooq.*
import org.jooq.impl.DSL
import kotlin.reflect.KMutableProperty0

/**
 * @author : Kairlec
 * @since : 2022/3/1
 **/
@Suppress("NOTHING_TO_INLINE")
internal inline fun <T> T?.setNotNull(function: KMutableProperty0<T>) {
    if (this != null) {
        function.set(this)
    }
}

typealias RecordUpdateDSL<T> = T.() -> Unit


@RequiresOptIn("这个接口很危险,你要确定你在做什么", level = RequiresOptIn.Level.ERROR)
annotation class HazardousAPI

fun <P : RowCountQuery, T> P.fold(list: Collection<T>, body: P.(T) -> P): P =
    list.fold(this, body)

fun <T> values(field: Field<T>): Field<T> {
    return DSL.field("VALUES({0})", field.dataType, field)
}

fun <T> Field<T>.ref(): Field<T> = values(this)

fun <T> InsertOnDuplicateSetStep<out Record>.ref(vararg fields: TableField<out Record, T>): InsertOnDuplicateSetMoreStep<out Record> {
    return fields.fold(this) { step, field ->
        step.set(field, field.ref())
    } as InsertOnDuplicateSetMoreStep<out Record>
}

fun Query.executeBool() = execute() > 0

fun UpdatableRecord<*>.updateBool() = update() > 0

fun <T> ResultQuery<Record1<T>>.fetchOrNull() = fetchOne()?.value1()