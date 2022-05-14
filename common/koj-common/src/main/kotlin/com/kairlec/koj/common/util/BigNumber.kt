package com.kairlec.koj.backend.util

import java.math.BigInteger

private val LongMaxValue: BigInteger = Long.MAX_VALUE.toBigInteger()

fun ULong.toBigInteger(): BigInteger {
    val data = this.toLong()
    return if (data >= 0) data.toBigInteger()
    else LongMaxValue + (data - Long.MAX_VALUE).toBigInteger()
}

private val ULongValuesRange = BigInteger.ZERO..(BigInteger.TWO * LongMaxValue + BigInteger.ONE)

fun BigInteger.toULong(): ULong {
    require(this in ULongValuesRange)
    return toLong().toULong()
}