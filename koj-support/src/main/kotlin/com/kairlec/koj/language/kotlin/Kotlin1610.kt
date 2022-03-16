package com.kairlec.koj.language.kotlin

import com.kairlec.koj.language.java.Java

abstract class Kotlin1610(
    override val baseJava: Java
) : Kotlin(baseJava) {
    final override val version: String get() = "1.6.10"
}