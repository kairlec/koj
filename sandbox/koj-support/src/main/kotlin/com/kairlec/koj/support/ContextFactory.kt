package com.kairlec.koj.support


import com.kairlec.koj.core.*
import com.kairlec.koj.language.Jvm
import com.kairlec.koj.language.c.C
import com.kairlec.koj.language.cpp.CPP
import com.kairlec.koj.support.compiler.gcc.GCCCompileConfig
import com.kairlec.koj.support.compiler.jvm.JvmCompileConfig

object ClikeContextFactory : KojContextFactory {
    override fun createCompileConfig(context: KojContext): CompileConfig {
        return GCCCompileConfig(CompileSource(context.code), debug = context.debug)
    }

    override fun isSupported(language: Language): Boolean {
        return language is C || language is CPP
    }
}

object JvmContextFactory : KojContextFactory {
    override fun createCompileConfig(context: KojContext): CompileConfig {
        return JvmCompileConfig(CompileSource(context.code), debug = context.debug)
    }

    override fun isSupported(language: Language): Boolean {
        return language is Jvm
    }
}