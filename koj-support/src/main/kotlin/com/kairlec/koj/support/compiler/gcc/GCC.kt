package com.kairlec.koj.support.compiler.gcc

import com.google.auto.service.AutoService
import com.kairlec.koj.core.*
import com.kairlec.koj.language.c.C
import com.kairlec.koj.language.c.C11
import com.kairlec.koj.language.c.C99
import com.kairlec.koj.language.cpp.*
import com.kairlec.koj.sandbox.docker.Docker
import com.kairlec.koj.sandbox.docker.DockerSandboxCompileConfig
import com.kairlec.koj.sandbox.docker.KOJEnv
import com.kairlec.koj.support.MB
import com.kairlec.koj.support.compiler.AbstractCompileConfig
import com.kairlec.koj.support.s
import mu.KotlinLogging

data class GCCCompileSuccess(
    val executableName: String,
    val image: String
) : CompileSuccess

data class GCCCompileFailure(override val message: String, override val cause: Throwable?) : CompileFailure

data class GCCCompileConfig(
    override val source: CompileSource,
    override val compileImage: String = "kairlec/koj-clike",
    override val compileImageVersion: String = "latest"
) : AbstractCompileConfig()

abstract class GCC : KojCompiler {
    context(KojContext) override suspend fun compile(compileConfig: CompileConfig): CompileResult {
        require(compileConfig is GCCCompileConfig) { "GCCCompileConfig required" }
        val language = useLanguage
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
                useLanguage,
                "current gcc impl is not supported for this language yet"
            )
        }
        val (define, compiler, suffix) = when (language) {
            is C -> Triple(language.preDefine, "gcc", "c")
            is CPP -> Triple(language.preDefine, "g++", "cc")
            else -> Triple(emptyList(), "", "")
        }
        val sourceFileName = "main.${suffix}"
        val compileArguments = buildList {
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
            add(executableName)
            add(sourceFileName)
        }
        val image = "${compileConfig.compileImage}:${compileConfig.compileImageVersion}"
        val output = Docker.compile(
            tempDirectory,
            DockerSandboxCompileConfig(
                sourceFileName = sourceFileName,
                sourceContent = compileConfig.source.source,
                namespace = "${namespace}-${id}",
                image = image,
                kojEnv = KOJEnv(50.s, 60.s, 256.MB, -1, -1, 10.MB, false, compiler, compileArguments, emptyList())
            )
        )
        return if (output.isError()) {
            log.warn { "compile error :$output" }
            val exception = SandboxCompileException(output)
            GCCCompileFailure(exception.message, exception)
        } else {
            GCCCompileSuccess(executableName, image)
        }
    }

    override fun isSupported(language: Language): Boolean {
        return language is C || language is CPP
    }

    companion object {
        private val log = KotlinLogging.logger {}
        private const val executableName = "main"
    }
}

@AutoService(KojCompiler::class)
class GCCForC(override val name: String = "gcc") : GCC()

@AutoService(KojCompiler::class)
class GCCForCpp(override val name: String = "g++") : GCC()