package com.kairlec.koj.dao.extended

import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import org.jooq.*
import org.jooq.impl.DSL

/**
 * @author : Kairlec
 * @since : 2022/3/1
 **/
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

suspend fun Publisher<Int>.awaitBool() = awaitSingle() > 0

suspend fun <T> ResultQuery<Record1<T>>.awaitOrNull() = awaitFirstOrNull()?.value1()
suspend fun <T> ResultQuery<Record1<T>>.awaitOrNull(default: T) = awaitFirstOrNull()?.value1() ?: default