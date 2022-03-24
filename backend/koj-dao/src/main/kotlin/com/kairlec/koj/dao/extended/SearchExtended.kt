@file:Suppress("UNCHECKED_CAST", "unused")

package com.kairlec.koj.dao.extended

import mu.KotlinLogging
import org.jooq.Condition
import org.jooq.Field
import org.jooq.Record
import org.jooq.Table
import org.jooq.impl.DSL

/**
 * @author : Kairlec
 * @since : 2022/3/2
 **/

private val log = KotlinLogging.logger { }

data class FieldConditon(
    val field: Field<*>,
    val word: String,
    val preciseMode: SearchCondition.SearchMode
)

data class SearchCondition(
    val key: String,
    val word: String,
    val preciseMode: SearchMode
) {
    fun interface SearchMode {
        fun search(field: Field<*>, word: String): Condition

        fun escape(word: String, escape: Char = '\\'): String {
            return DSL.escape(word, escape)
        }
    }

    /**
     * 原始模式,没有转义,这个接口很危险,必须要明白自己在做什么
     */
    @HazardousAPI
    object NoEscapeMode : SearchMode {
        override fun search(field: Field<*>, word: String): Condition {
            return field.like(word)
        }
    }

    /**
     * 精准搜索
     */
    object PreciseMode : SearchMode {
        override fun search(field: Field<*>, word: String): Condition {
            return field.like(escape(word))
        }
    }

    /**
     * 模糊搜索
     */
    object FuzzyMode : SearchMode {
        override fun search(field: Field<*>, word: String): Condition {
            return field.like("%${escape(word)}%")
        }
    }

    /**
     * 开头
     */
    object StartWithMode : SearchMode {
        override fun search(field: Field<*>, word: String): Condition {
            return field.like("${escape(word)}%")
        }
    }

    /**
     * 结尾
     */
    object EndWithMode : SearchMode {
        override fun search(field: Field<*>, word: String): Condition {
            return field.like("%${escape(word)}")
        }
    }

    fun interface SearchModeParser {
        fun parser(char: Char): SearchMode?
    }

    companion object : SearchModeParser {
        @OptIn(HazardousAPI::class)
        override fun parser(char: Char): SearchMode? {
            return when (char) {
                '0' -> FuzzyMode
                '1' -> PreciseMode
                '2' -> StartWithMode
                '3' -> EndWithMode
                '4' -> NoEscapeMode
                else -> null
            }
        }
    }
}

fun String.toCondition(searchModeParser: SearchCondition.SearchModeParser = SearchCondition): List<SearchCondition> {
    return split(',').mapNotNull {
        if (it.isEmpty()) {
            null
        } else {
            val keyPair = it.split('=')
            if (keyPair.size != 2) {
                log.debug { "search expression syntax wrong(no `=`):${it}" }
                null
            } else {
                val (key, word) = keyPair
                if (key.length < 2 || word.isEmpty()) {
                    log.debug { "search expression syntax wrong(no search mode or key):${it}" }
                    null
                } else {
                    val searchMode = searchModeParser.parser(key[0])
                    if (searchMode == null) {
                        log.warn { "search expression syntax wrong(unsupport search mode):${key[0]}" }
                        null
                    } else {
                        SearchCondition(
                            key.substring(1),
                            word,
                            searchMode
                        )
                    }
                }
            }
        }
    }
}

fun Table<out Record>.search(
    search: String?,
    any: Boolean = false,
    searchModeParser: SearchCondition.SearchModeParser = SearchCondition,
    mapCondition: (List<SearchCondition>) -> List<SearchCondition> = { it },
    mapField: (List<FieldConditon>) -> List<FieldConditon> = { it }
): Condition {
    if (search == null) {
        return DSL.trueCondition()
    }
    if (search.isEmpty()) {
        return DSL.falseCondition()
    }
    val tableFieldNames = fieldsSet()
    val conditions = search.toCondition(searchModeParser).let(mapCondition).mapNotNull { (name, word, precise) ->
        tableFieldNames[name].also {
            if (it == null) {
                log.debug { "search expression syntax wrong(${name} is not table field), valid fields:${tableFieldNames.keys}" }
            }
        }?.let { FieldConditon(it, word, precise) }
    }.let(mapField)
    if (conditions.isEmpty()) {
        return DSL.falseCondition()
    }
    return if (any) {
        val condition: Condition = DSL.falseCondition()
        conditions.fold(condition) { acc, (key, word, searchMode) ->
            acc.or(searchMode.search(key, word))
        }
    } else {
        val condition: Condition = DSL.trueCondition()
        conditions.fold(condition) { acc, (key, word, searchMode) ->
            acc.and(searchMode.search(key, word))
        }
    }
}
