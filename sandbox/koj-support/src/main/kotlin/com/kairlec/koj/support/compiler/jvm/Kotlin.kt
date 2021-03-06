package com.kairlec.koj.support.compiler.jvm


import com.kairlec.koj.core.*
import com.kairlec.koj.language.kotlin.Kotlin
import com.kairlec.koj.language.kotlin.Kotlin1610_Java11
import com.kairlec.koj.language.kotlin.Kotlin1610_Java17
import com.kairlec.koj.language.kotlin.Kotlin1610_Java8
import com.kairlec.koj.sandbox.docker.Docker
import com.kairlec.koj.sandbox.docker.DockerSandboxCompileConfig
import com.kairlec.koj.sandbox.docker.KOJEnv
import com.kairlec.koj.support.MB
import com.kairlec.koj.support.s
import mu.KotlinLogging

object Kotlin : KojCompiler {
    override val name get() = "kotlin"

    override suspend fun compile(context: KojContext, compileConfig: CompileConfig): CompileResult {
        require(compileConfig is JvmCompileConfig) { "JvmCompileConfig required" }
        val imageVersion = if (compileConfig.compileImageVersion.isEmpty()) {
            when (context.useLanguage) {
                is Kotlin1610_Java8 -> "jvm8"
                is Kotlin1610_Java11 -> "jvm11"
                is Kotlin1610_Java17 -> "jvm17"
                else -> throw UnsupportedLanguageException(
                    context.useLanguage,
                    "current jvm impl is not supported for this language yet"
                )
            }
        } else {
            when (context.useLanguage) {
                is Kotlin1610_Java8 -> {
                    require(compileConfig.compileImageVersion == "jvm8") { "Java8 requires compileImageVersion == jvm8" }
                }
                is Kotlin1610_Java11 -> {
                    require(compileConfig.compileImageVersion == "jvm11") { "Java11 requires compileImageVersion == jvm11" }
                }
                is Kotlin1610_Java17 -> {
                    require(compileConfig.compileImageVersion == "jvm17") { "Java17 requires compileImageVersion == jvm17" }
                }
                else -> throw UnsupportedLanguageException(
                    context.useLanguage,
                    "current jvm impl is not supported for this language yet"
                )
            }
            compileConfig.compileImageVersion
        }
        val sourceFileName = "Main.kt"
        val compileArguments = buildList {
            add(sourceFileName)
        }
        val image = "${compileConfig.compileImage}:${imageVersion}"
        val output = Docker.compile(
            context.tempDirectory,
            DockerSandboxCompileConfig(
                sourceFileName = sourceFileName,
                sourceContent = compileConfig.source.source,
                namespace = "${context.namespace}-${context.id}",
                image = image,
                kojEnv = KOJEnv(
                    keepStdin = true,
                    keepStdout = true,
                    maxCpuTime = 180.s,
                    maxRealTime = 180.s,
                    maxMemory = 512.MB,
                    maxStack = -1,
                    maxProcessNumber = -1,
                    maxOutputSize = 256.MB,
                    memoryCheckOnly = true,
                    exePath = "/usr/sbin/kotlinc",
                    addonPath = "",
                    args = compileArguments,
                    env = emptyList()
                ),
                debug = compileConfig.debug
            )
        )
        return if (output.isError()) {
            log.warn { "compile error :$output" }
            val exception = SandboxCompileException(output)
            JvmCompileFailure(exception.message, exception, output.stdout?.value ?: "", output.stderr?.value ?: "")
        } else {
            JvmCompileSuccess(mainClassQualifierName, image, output.stdout?.value ?: "", output.stderr?.value ?: "")
        }
    }

    override fun isSupported(language: Language): Boolean {
        return language is Kotlin
    }

    private val log = KotlinLogging.logger {}
    private const val mainClassQualifierName = "MainKt"
}
