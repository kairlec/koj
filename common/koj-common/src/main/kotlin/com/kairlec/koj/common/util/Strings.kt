@file:JvmName("StringUtil")

package com.kairlec.koj.common.util

/**
 * @author : Kairlec
 * @since : 2022/2/11
 **/
@JvmField
val lowerCharRange = CharRange('a', 'z')

@JvmField
val upperCharRange = CharRange('A', 'Z')

@JvmField
val digestCharRange = CharRange('0', '9')

@JvmName("random")
fun randomString(size: Int, vararg charRanges: CharRange): String {
    require(charRanges.isNotEmpty()) { "charRanges must not be empty" }
    return buildString(size) {
        repeat(size) {
            append(charRanges.random().random())
        }
    }
}

@JvmName("random")
fun randomString(size: Int): String {
    return randomString(size, lowerCharRange, upperCharRange, digestCharRange)
}

@JvmName("randomTik")
@Deprecated(
    message = "生成的字符会带有_,这在某些地方可能是比较棘手的,建议直接随机字符串",
    replaceWith = ReplaceWith("randomString(32)"),
    level = DeprecationLevel.ERROR,
)
fun randomTikString(): String {
    return "${randomString(2)}_${randomString(14)}_${randomString(16)}__"
}