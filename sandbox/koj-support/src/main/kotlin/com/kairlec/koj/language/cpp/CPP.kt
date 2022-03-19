package com.kairlec.koj.language.cpp

import com.kairlec.koj.core.Language

abstract class CPP : Language {
    final override val name: String get() = "C++"
    final override val extension: String get() = ".cc"
    override val id: String get() = "C++$version"
    open val preDefine: List<String> = listOf("ONLINE_JUDGE")

    override fun toString(): String {
        return "$name ($version)"
    }
}