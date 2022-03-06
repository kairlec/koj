package com.kairlec.koj.support.compiler.gcc


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
    override suspend fun compile(context: KojContext, compileConfig: CompileConfig): CompileResult {
        require(compileConfig is GCCCompileConfig) { "GCCCompileConfig required" }
        val language = context.useLanguage
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
                language,
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
            context.tempDirectory,
            DockerSandboxCompileConfig(
                sourceFileName = sourceFileName,
                sourceContent = compileConfig.source.source,
                namespace = "${context.namespace}-${context.id}",
                image = image,
                kojEnv = KOJEnv(
                    keepStdin = true,
                    maxCpuTime = 50.s,
                    maxRealTime = 60.s,
                    maxMemory = 256.MB,
                    maxStack = -1,
                    maxProcessNumber = -1,
                    maxOutputSize = 10.MB,
                    memoryCheckOnly = false,
                    exePath = compiler,
                    args = compileArguments,
                    env = emptyList()
                )
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

object GCCForC : GCC() {
    override val name get() = "gcc"
}

object GCCForCpp : GCC() {
    override val name get() = "g++"
}