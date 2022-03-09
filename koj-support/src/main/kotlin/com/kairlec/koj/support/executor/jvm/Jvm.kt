package com.kairlec.koj.support.executor.jvm


import com.kairlec.koj.common.TempDirectory
import com.kairlec.koj.core.*
import com.kairlec.koj.language.Jvm
import com.kairlec.koj.language.kotlin.Kotlin1610_Java8
import com.kairlec.koj.sandbox.docker.Docker
import com.kairlec.koj.sandbox.docker.DockerSandboxInitConfig
import com.kairlec.koj.sandbox.docker.DockerSandboxRunConfig
import com.kairlec.koj.sandbox.docker.KOJEnv
import com.kairlec.koj.support.compiler.jvm.JvmCompileSuccess
import com.kairlec.koj.support.toExecuteResultType
import kotlin.io.path.absolutePathString


data class JvmExecuteSuccess(
    override var type: ExecuteResultType,
    override val stdout: String,
) : ExecuteSuccess

data class JvmExecuteFailure(
    val stdout: String?,
    val log: String?,
    val code: Int,
    override val message: String,
    override val cause: Throwable?,
    override val failureType: ExecuteResultType
) : ExecuteFailure

object Jvm : KojExecutor {
    override val name get() = "jvm"

    override suspend fun execute(
        context: KojContext,
        compileSuccess: CompileSuccess,
        input: String,
        config: ExecutorConfig,
    ): KojExecuteResult {
        require(compileSuccess is JvmCompileSuccess)
        val classFilename = "${compileSuccess.mainClassQualifierName}.class"
        val args = buildList {
            if (config.maxMemory > 0) {
                add("-XX:MaxRAM=${config.maxMemory}")
            }
            add("-Djava.security.manager")
            add("-Djava.security.policy=/etc/policy/java.policy")
            add("-Dfile.encoding=UTF-8")
            add(compileSuccess.mainClassQualifierName)
            addAll(config.args)
        }

        val env = buildSet {
            (context.useLanguage as Jvm).systemPropertyDefine.forEach { (key, value) ->
                add("$key=$value")
            }
            addAll(config.env)
        }.toList()
        val classFilePath = context.tempDirectory.resolve(classFilename).absolutePathString()
        val output = Docker.run(
            context.tempDirectory,
            DockerSandboxRunConfig(
                image = compileSuccess.image,
                namespace = "${context.namespace}-${context.id}",
                exeMount = classFilePath to classFilename,
                input = input,
                kojEnv = KOJEnv(
                    keepStdin = false,
                    maxCpuTime = config.maxTime,
                    maxRealTime = config.maxTime * 2,
                    maxMemory = -1,
                    maxStack = config.maxStack,
                    maxProcessNumber = config.maxProcessNumber,
                    maxOutputSize = config.maxOutputSize,
                    memoryCheckOnly = true,
                    exePath = "/usr/sbin/java",
                    args = args,
                    env = env,
                )
            )
        )
        return if (output.isError()) {
            JvmExecuteFailure(
                output.stdout?.value,
                output.logging?.value,
                output.exitCode(),
                output.status?.wrong ?: "",
                SandboxExecuteException(output),
                output.status?.result?.toExecuteResultType() ?: ExecuteResultType.SE
            )
        } else {
            JvmExecuteSuccess(
                output.status?.result?.toExecuteResultType() ?: ExecuteResultType.AC,
                output.stdout?.value ?: ""
            )
        }
    }

    override fun isSupported(language: Language): Boolean {
        return language is Jvm
    }
}

suspend fun main() {
    Docker.init(DockerSandboxInitConfig(emptyList()))
    val context = KojFactory.create(
        id = 123456,
        "kairlec",
        TempDirectory.create("123456"),
        Kotlin1610_Java8,
        """
            fun main(){
                println(readln())
            }
        """.trimIndent(),
        Problem(123L,
            "hello koj\n",
            false,
            buildMap {
                put(Kotlin1610_Java8, RunConfig(-1, -1, -1))
            }
        )
    )
    context.run()
}
