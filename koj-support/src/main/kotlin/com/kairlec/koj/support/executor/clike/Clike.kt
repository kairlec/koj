package com.kairlec.koj.support.executor.clike


import com.kairlec.koj.core.*
import com.kairlec.koj.language.c.C
import com.kairlec.koj.language.cpp.CPP
import com.kairlec.koj.sandbox.docker.Docker
import com.kairlec.koj.sandbox.docker.DockerSandboxRunConfig
import com.kairlec.koj.sandbox.docker.KOJEnv
import com.kairlec.koj.support.compiler.gcc.GCCCompileSuccess
import com.kairlec.koj.support.toExecuteResultType
import kotlin.io.path.absolutePathString

data class ClikeExecuteSuccess(
    override var type: ExecuteResultType,
    override val stdout: String,
) : ExecuteSuccess

data class ClikeExecuteFailure(
    val stdout: String?,
    val log: String?,
    val code: Int,
    override val message: String,
    override val cause: Throwable?,
    override val failureType: ExecuteResultType
) : ExecuteFailure

object Clike : KojExecutor {
    override val name get() = "clike"

    override suspend fun execute(
        context: KojContext,
        compileSuccess: CompileSuccess,
        input: String,
        config: ExecutorConfig,
    ): KojExecuteResult {
        require(compileSuccess is GCCCompileSuccess)
        val executablePath = context.tempDirectory.resolve(compileSuccess.executableName).absolutePathString()
        val output = Docker.run(
            context.tempDirectory,
            DockerSandboxRunConfig(
                image = compileSuccess.image,
                namespace = "${context.namespace}-${context.id}",
                exeMount = executablePath to compileSuccess.executableName,
                input = input,
                kojEnv = KOJEnv(
                    keepStdin = false,
                    maxCpuTime = config.maxTime,
                    maxRealTime = config.maxTime * 2,
                    maxMemory = config.maxMemory,
                    maxStack = config.maxStack,
                    maxProcessNumber = config.maxProcessNumber,
                    maxOutputSize = config.maxOutputSize,
                    memoryCheckOnly = false,
                    exePath = compileSuccess.executableName,
                    args = config.args,
                    env = config.env,
                )
            )
        )
        return if (output.isError()) {
            ClikeExecuteFailure(
                output.stdout?.value,
                output.logging?.value,
                output.exitCode(),
                output.status?.wrong ?: "",
                SandboxExecuteException(output),
                output.status?.result?.toExecuteResultType() ?: ExecuteResultType.SE
            )
        } else {
            ClikeExecuteSuccess(
                output.status?.result?.toExecuteResultType() ?: ExecuteResultType.AC,
                output.stdout?.value ?: ""
            )
        }
    }

    override fun isSupported(language: Language): Boolean {
        return language is C || language is CPP
    }
}