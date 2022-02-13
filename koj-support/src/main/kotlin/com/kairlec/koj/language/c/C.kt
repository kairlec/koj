package com.kairlec.koj.language.c

import com.kairlec.koj.core.Language

abstract class C : Language {
    final override val name: String get() = "C"
    final override val extension: String get() = ".c"

    open val preDefine: List<String> = listOf("ONLINE_JUDGE")

    override fun toString(): String {
        return "$name ($version)"
    }
}