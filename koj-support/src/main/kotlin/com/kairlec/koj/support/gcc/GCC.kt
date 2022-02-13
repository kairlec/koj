package com.kairlec.koj.support.gcc

import com.kairlec.koj.core.*
import com.kairlec.koj.language.c.C
import com.kairlec.koj.language.c.C11
import com.kairlec.koj.language.c.C99
import com.kairlec.koj.language.cpp.*

abstract class GCC : AbstractKojCompiler() {
    override fun internalCompile(compileConfig: CompileConfig): CompileResult {
        val language = compileConfig.language
        val stdVersion = when (language) {
            is C99 -> "c99"
            is C11 -> "c11"
            is CPP98 -> "c++98"
            is CPP03 -> "c++03"
            is CPP11 -> "c++11"
            is CPP14 -> "c++14"
            is CPP17 -> "c++17"
            is CPP20 -> "c++20"
            else -> throw UnsupportedLanguageException(
                compileConfig.language,
                "current gcc impl is not supported for this language yet"
            )
        }
        val (define, compiler) = when (language) {
            is C -> language.preDefine to "gcc"
            is CPP -> language.preDefine to "g++"
            else -> emptyList<String>() to ""
        }
        val compileArguments = buildList {
            add(compiler)
            add("-std=${stdVersion}")
            add("-Wall")
            define.forEach {
                add("-D$it")
            }
            add("-O2")
            add("-lm")
            add("-march=native")
            add("-s")
            add("-o")
            add("main")
            add("main.c")
        }
    }

    override fun isSupported(language: Language): Boolean {
        return language is C || language is CPP
    }
}
