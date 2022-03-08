package com.kairlec.koj.support.compiler.jvm


import com.kairlec.koj.core.*
import com.kairlec.koj.language.java.Java
import com.kairlec.koj.language.java.Java11
import com.kairlec.koj.language.java.Java17
import com.kairlec.koj.language.java.Java8
import com.kairlec.koj.sandbox.docker.Docker
import com.kairlec.koj.sandbox.docker.DockerSandboxCompileConfig
import com.kairlec.koj.sandbox.docker.KOJEnv
import com.kairlec.koj.support.MB
import com.kairlec.koj.support.compiler.AbstractCompileConfig
import com.kairlec.koj.support.s
import mu.KotlinLogging

data class JvmCompileSuccess(
    val mainClassQualifierName: String,
    val image: String
) : CompileSuccess

data class JvmCompileFailure(override val message: String, override val cause: Throwable?) : CompileFailure

data class JvmCompileConfig(
    override val source: CompileSource,
    override val compileImage: String = "kairlec/koj-jvm",
    override val compileImageVersion: String = ""
) : AbstractCompileConfig()

object Java : KojCompiler {
    override val name get() = "openjdk"

    override suspend fun compile(context: KojContext, compileConfig: CompileConfig): CompileResult {
        require(compileConfig is JvmCompileConfig) { "JvmCompileConfig required" }
        val imageVersion = if (compileConfig.compileImageVersion.isEmpty()) {
            when (context.useLanguage) {
                is Java8 -> "8"
                is Java11 -> "11"
                is Java17 -> "17"
                else -> throw UnsupportedLanguageException(
                    context.useLanguage,
                    "current jvm impl is not supported for this language yet"
                )
            }
        } else {
            when (context.useLanguage) {
                is Java8 -> {
                    require(compileConfig.compileImageVersion == "8") { "Java8 requires compileImageVersion == 8" }
                }
                is Java11 -> {
                    require(compileConfig.compileImageVersion == "11") { "Java11 requires compileImageVersion == 11" }
                }
                is Java17 -> {
                    require(compileConfig.compileImageVersion == "17") { "Java17 requires compileImageVersion == 17" }
                }
                else -> throw UnsupportedLanguageException(
                    context.useLanguage,
                    "current jvm impl is not supported for this language yet"
                )
            }
            compileConfig.compileImageVersion
        }
        val sourceFileName = "Main.java"
        val compileArguments = buildList {
            add(sourceFileName)
            add("-encoding")
            add("UTF8")
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
                    maxCpuTime = 50.s,
                    maxRealTime = 60.s,
                    maxMemory = 256.MB,
                    maxStack = -1,
                    maxProcessNumber = -1,
                    maxOutputSize = 10.MB,
                    memoryCheckOnly = true,
                    exePath = "/usr/sbin/javac",
                    args = compileArguments,
                    env = emptyList()
                )
            )
        )
        return if (output.isError()) {
            log.warn { "compile error :$output" }
            val exception = SandboxCompileException(output)
            JvmCompileFailure(exception.message, exception)
        } else {
            JvmCompileSuccess(mainClassQualifierName, image)
        }
    }

    override fun isSupported(language: Language): Boolean {
        return language is Java
    }

    private val log = KotlinLogging.logger {}
    private const val mainClassQualifierName = "Main"
}
