package com.kairlec.koj.language.java

import com.kairlec.koj.language.Jvm

abstract class Java : Jvm() {
    override val name: String
        get() = "Java"
    override val extension: String
        get() = ".java"

    override fun toString(): String {
        return "Java ($version)"
    }
}