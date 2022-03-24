package com.kairlec.koj.common.enums

import kotlin.reflect.full.findAnnotation

/**
 * @author : Kairlec
 * @since : 2022/2/14
 **/
@Suppress("MemberVisibilityCanBePrivate")
object BaseEnumFactory {
    @JvmStatic
    fun <E> smartParse(raw: String, enumClass: Class<E>): E where E : Enum<E>, E : BaseEnum {
        return try {
            val code = raw.toInt()
            valueOf(code, enumClass)
        } catch (e: NumberFormatException) {
            valueOf(raw, enumClass)
        }
    }

    @JvmStatic
    inline fun <reified E> smartParse(raw: String): E where E : Enum<E>, E : BaseEnum {
        return smartParse(raw, E::class.java)
    }

    @JvmStatic
    fun <E> smartParseOrNull(raw: String, enumClass: Class<E>): E? where E : Enum<E>, E : BaseEnum {
        return try {
            val code = raw.toInt()
            valueOfOrNull(code, enumClass)
        } catch (e: NumberFormatException) {
            valueOfOrNull(raw, enumClass)
        }
    }

    @JvmStatic
    inline fun <reified E> smartParseOrNull(raw: String): E? where E : Enum<E>, E : BaseEnum {
        return smartParseOrNull(raw, E::class.java)
    }

    @JvmStatic
    fun <E> valueOf(code: Int, enumClass: Class<E>): E where E : Enum<E>, E : BaseEnum {
        val name = enumClass.getAnnotation<BaseEnumName>()?.value ?: enumClass.simpleName
        return valueOfOrNull(code, enumClass) ?: throw IllegalArgumentException("${name}代码解析失败:$code")
    }

    @JvmStatic
    inline fun <reified E> valueOf(code: Int): E where E : Enum<E>, E : BaseEnum {
        val name = E::class.findAnnotation<BaseEnumName>()?.value ?: E::class.simpleName
        return valueOfOrNull(code) ?: throw IllegalArgumentException("${name}代码解析失败:$code")
    }

    @JvmStatic
    fun <E> valueOfOrNull(code: Int, enumClass: Class<E>): E? where E : Enum<E>, E : BaseEnum {
        val enums = enumClass.enumConstants
        for (e in enums) {
            if (e.code == code) {
                return e
            }
        }
        return null
    }

    @JvmStatic
    inline fun <reified E> valueOfOrNull(code: Int): E? where E : Enum<E>, E : BaseEnum {
        val enums = E::class.java.enumConstants
        for (e in enums) {
            if (e.code == code) {
                return e
            }
        }
        return null
    }

    @JvmStatic
    fun <E> valueOf(name: String, enumClass: Class<E>): E where E : Enum<E>, E : BaseEnum {
        val enumName = enumClass.getAnnotation<BaseEnumName>()?.value ?: enumClass.simpleName
        return valueOfOrNull(name, enumClass) ?: throw IllegalArgumentException("${enumName}名称解析失败:$name")
    }

    @JvmStatic
    inline fun <reified E> valueOf(name: String): E where E : Enum<E>, E : BaseEnum {
        val enumName = E::class.findAnnotation<BaseEnumName>()?.value ?: E::class.simpleName
        return valueOfOrNull(name) ?: throw IllegalArgumentException("${enumName}名称解析失败:$name")
    }

    @JvmStatic
    fun <E> valueOfOrNull(name: String, enumClass: Class<E>): E? where E : Enum<E>, E : BaseEnum {
        val enums = enumClass.enumConstants
        for (e in enums) {
            if (e.value.equals(name, ignoreCase = true) ||
                e.friendName.equals(name, ignoreCase = true) ||
                e.name.equals(name, ignoreCase = true)
            ) {
                return e
            }
        }
        return null
    }

    @JvmStatic
    inline fun <reified E> valueOfOrNull(name: String): E? where E : Enum<E>, E : BaseEnum {
        val enums = E::class.java.enumConstants
        for (e in enums) {
            if (e.value.equals(name, ignoreCase = true) ||
                e.friendName.equals(name, ignoreCase = true) ||
                e.name.equals(name, ignoreCase = true)
            ) {
                return e
            }
        }
        return null
    }

    @JvmStatic
    private inline fun <reified T : Annotation> Class<*>.getAnnotation(): T? {
        return getAnnotation(T::class.java)
    }
}

