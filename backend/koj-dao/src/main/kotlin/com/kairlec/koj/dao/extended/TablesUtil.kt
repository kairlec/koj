@file:Suppress("UNCHECKED_CAST")

package com.kairlec.koj.dao.extended

import mu.KotlinLogging
import org.jooq.Record
import org.jooq.Table
import org.jooq.TableField
import org.jooq.impl.TableImpl
import org.reflections.Reflections
import org.reflections.scanners.Scanners
import org.reflections.util.ConfigurationBuilder
import java.lang.reflect.Modifier

/**
 * @author : Kairlec
 * @since : 2022/3/2
 **/

object TablesUtil {
    private val log = KotlinLogging.logger { }

    // 并发安全要个屁,多次初始化就多次初始化,并发map影响性能
    private val tableFields: MutableMap<Table<*>, FieldSet<*>> = mutableMapOf()

    init {
        register("com.geniuspros.robot.oauth")
    }

    fun register(vararg packageName: String) {
        try {
            Class.forName("org.reflections.Reflections")
            log.debug { "use reflection to search table" }
            val reflections = Reflections(
                ConfigurationBuilder().forPackages(*packageName)
                    .setScanners(Scanners.SubTypes)
            )
            val classes = reflections.getSubTypesOf(TableImpl::class.java)
            classes.forEach { tableClass ->
                val tableField =
                    tableClass.fields.firstOrNull { Modifier.isStatic(it.modifiers) && it.type == tableClass }
                if (tableField == null) {
                    log.warn { "table class $tableClass has no static field" }
                    return@forEach
                }
                log.debug { "table class $tableClass register in table util" }
                val table = tableField.get(null) as Table<*>
                tableFields[table] = table._fieldsSet()
            }
        } catch (e: ClassNotFoundException) {
            log.warn { "no \"org.reflections.Reflections\" found, use lazy init for tables util" }
        } catch (e: IncompatibleClassChangeError) {
            log.error(e) { "load reflections error" }
        }
    }

    fun <R : Record> getTableFields(table: Table<R>): FieldSet<R> {
        return (tableFields[table] ?: table._fieldsSet().also {
            log.info { "use lazy init for tables util:${table.name}" }
            tableFields[table] = it
        }) as FieldSet<R>
    }
}

class FieldSet<R : Record>(private val fields: Map<String, TableField<R, *>>) :
    Map<String, TableField<R, *>> by fields, Set<String> by fields.keys {
    override val size: Int
        get() = fields.size

    override fun isEmpty(): Boolean {
        return fields.isEmpty()
    }

    override fun contains(element: String): Boolean {
        return fields.containsKey(element.toSimplify())
    }

    override fun containsKey(key: String): Boolean {
        return fields.containsKey(key.toSimplify())
    }

    override fun get(key: String): TableField<R, *>? {
        return fields[key.toSimplify()]
    }
}

private fun String.toSimplify(): String {
    return replace("[-_#]".toRegex(), "").lowercase()
}

private fun <R : Record> Table<R>._fieldsSet(): FieldSet<R> {
    return FieldSet(this.fields().associate {
        it.name.toSimplify() to (it as TableField<R, *>)
    })
}

fun <R : Record> Table<R>.fieldsSet(): FieldSet<R> {
    return TablesUtil.getTableFields(this)
}