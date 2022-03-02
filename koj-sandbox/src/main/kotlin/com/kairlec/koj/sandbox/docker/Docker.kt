package com.kairlec.koj.sandbox.docker

import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.api.exception.NotFoundException
import com.github.dockerjava.api.model.AccessMode
import com.github.dockerjava.api.model.Bind
import com.github.dockerjava.api.model.HostConfig
import com.github.dockerjava.api.model.Volume
import com.github.dockerjava.core.DefaultDockerClientConfig
import com.github.dockerjava.core.DockerClientImpl
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient
import com.kairlec.koj.common.TempDirectory
import com.kairlec.koj.sandbox.*
import kotlinx.coroutines.suspendCancellableCoroutine
import mu.KotlinLogging
import java.time.Duration
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.io.path.absolutePathString
import kotlin.io.path.readText

object Docker : Sandbox<DockerSandboxInitConfig, DockerSandboxRunConfig, DockerSandboxCompileConfig> {
    private val log = KotlinLogging.logger {}
    private lateinit var dockerClient: DockerClient


    override suspend fun init(initConfig: DockerSandboxInitConfig) {
        suspendCancellableCoroutine<Unit> { continuation ->
            try {
                val config = DefaultDockerClientConfig.createDefaultConfigBuilder().build()
                val dockerHttpClient = ApacheDockerHttpClient.Builder()
                    .dockerHost(config.dockerHost)
                    .sslConfig(config.sslConfig)
                    .maxConnections(100)
                    .connectionTimeout(Duration.ofSeconds(30))
                    .responseTimeout(Duration.ofSeconds(45))
                    .build()
                dockerClient = DockerClientImpl.getInstance(config, dockerHttpClient)
                val dockerVersion = dockerClient.versionCmd().exec()
                log.debug { "Sandbox of docker engine connect success using docker version: ${dockerVersion.version}" }

                initConfig.prepareImages.map {
                    val imagePullResult = dockerClient.pullImageCmd(it).start()
                    try {
                        imagePullResult.awaitCompletion()
                    } catch (e: Exception) {
                        log.error(e) { "prepare image for $it failed" }
                        throw DockerSandboxInitImageFailedException(it, e)
                    }
                }
                continuation.resume(Unit)
            } catch (e: Exception) {
                continuation.resumeWithException(e)
            }
        }
    }

    private fun <T> tryRead(message: String, block: () -> T): T? {
        return try {
            block()
        } catch (e: Exception) {
            log.error(e) { message }
            null
        }
    }

    override suspend fun run(tempDirectory: TempDirectory, runConfig: DockerSandboxRunConfig): SandboxOutput {
        return suspendCancellableCoroutine { continuation ->
            try {
                val stdin = tempDirectory.createFileWithContent("stdin", runConfig.input)
                val createResult = dockerClient.createContainerCmd(runConfig.image)
                    .withHostConfig(
                        HostConfig.newHostConfig()
                            .withBinds(
                                Bind(runConfig.exeMount.first, Volume(runConfig.exeMount.second), AccessMode.ro),
                                Bind(tempDirectory.absolutePathString(), Volume("/tmp/koj"), AccessMode.rw),
                                Bind(stdin.absolutePathString(), Volume("/tmp/koj/stdin"), AccessMode.ro),
                            )
                            .withAutoRemove(true)
                    )
                    .withEnv(runConfig.kojEnv.asList())
                    .withNetworkDisabled(true)
                    .withName(runConfig.namespace)
                    .exec()
                dockerClient.startContainerCmd(createResult.id).exec()
                val exitCode = try {
                    val waitResult = dockerClient.waitContainerCmd(createResult.id).start()
                    waitResult.awaitStatusCode()
                } catch (_: NotFoundException) {
                    0
                }
                val status = tryRead("load status file failed") {
                    Status.load(tempDirectory.resolve("status"))
                }
                val stdout = tryRead("load stdout file failed") {
                    Stdout(tempDirectory.resolve("stdout").readText())
                }
                val log = tryRead("load log file failed") {
                    Logging(tempDirectory.resolve("log").readText())
                }
                continuation.resume(SandboxOutput(exitCode, status, stdout, log))
            } catch (e: Exception) {
                continuation.resumeWithException(e)
            }
        }
    }

    override suspend fun compile(
        tempDirectory: TempDirectory,
        compileConfig: DockerSandboxCompileConfig
    ): SandboxOutput {
        return suspendCancellableCoroutine { continuation ->
            try {
                val sourceFile =
                    tempDirectory.createFileWithContent(compileConfig.sourceFileName, compileConfig.sourceContent)
                val createResult = dockerClient.createContainerCmd(compileConfig.image)
                    .withHostConfig(
                        HostConfig.newHostConfig()
                            .withBinds(
                                Bind(tempDirectory.absolutePathString(), Volume("/tmp/koj"), AccessMode.rw),
                                Bind(
                                    sourceFile.absolutePathString(),
                                    Volume("/tmp/koj/${compileConfig.sourceFileName}"),
                                    AccessMode.ro
                                ),
                            )
                            .withAutoRemove(true)
                    )
                    .withWorkingDir("/tmp/koj")
                    .withEnv(compileConfig.kojEnv.asList())
                    .withNetworkDisabled(true)
                    .withName(compileConfig.namespace)
                    .exec()
                dockerClient.startContainerCmd(createResult.id).exec()
                val exitCode = try {
                    val waitResult = dockerClient.waitContainerCmd(createResult.id).start()
                    waitResult.awaitStatusCode()
                } catch (_: NotFoundException) {
                    0
                }
                val status = tryRead("load status file failed") {
                    Status.load(tempDirectory.resolve("status"))
                }
                val stdout = tryRead("load stdout file failed") {
                    Stdout(tempDirectory.resolve("stdout").readText())
                }
                val log = tryRead("load log file failed") {
                    Logging(tempDirectory.resolve("log").readText())
                }
                continuation.resume(SandboxOutput(exitCode, status, stdout, log))
            } catch (e: Exception) {
                continuation.resumeWithException(e)
            }
        }
    }
}

suspend fun main() {
    Docker.init(DockerSandboxInitConfig(emptyList()))
    TempDirectory.create("kairlec").use {
        val output = Docker.run(
            it,
            DockerSandboxRunConfig(
                image = "koj-clike:latest",
                exeMount = "/home/kairlec/KOJ/sample/main" to "/root/main",
                namespace = "kairlec",
                input = "WOOOOOOORLD\n",
                kojEnv = KOJEnv(
                    -1,
                    -1,
                    -1,
                    -1,
                    -1,
                    -1,
                    true,
                    "/root/main",
                    emptyList(),
                    emptyList(),
                )
            )
        )
        println(output)
    }
}