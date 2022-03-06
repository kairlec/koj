package com.kairlec.koj.language.kotlin

import com.kairlec.koj.language.Jvm
import com.kairlec.koj.language.java.Java
import java.util.*

abstract class Kotlin(
    open val baseJava: Java
) : Jvm() {
    override val extension: String
        get() = ".kt"

    override val name: String
        get() = "Kotlin"

    override val systemPropertyDefine: Properties get() = baseJava.systemPropertyDefine

    override fun toString(): String {
        return "Kotlin ($version)"
    }
}