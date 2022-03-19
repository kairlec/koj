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
import kotlinx.coroutines.suspendCancellableCoroutine
import mu.KotlinLogging
import java.time.Duration
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.io.path.absolutePathString
import kotlin.io.path.readText

object Docker {
    private val log = KotlinLogging.logger {}
    private lateinit var dockerClient: DockerClient

    private const val containerPathPrefix = "/tmp/koj"

    fun resolve(path: String): String {
        return "$containerPathPrefix/$path"
    }

    suspend fun init(initConfig: DockerSandboxInitConfig) {
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
            log.error { "${message}: ${e.message}" }
            null
        }
    }

    suspend fun run(tempDirectory: TempDirectory, runConfig: DockerSandboxRunConfig): KojDockerOutput {
        return suspendCancellableCoroutine { continuation ->
            try {
                val stdin = tempDirectory.createFileWithContent("stdin", runConfig.input)
                log.debug { "create stdin file:${stdin} with content:${runConfig.input}" }
                val createResult = dockerClient.createContainerCmd(runConfig.image)
                    .withHostConfig(
                        HostConfig.newHostConfig()
                            .withBinds(
                                Bind(
                                    runConfig.exeMount.first,
                                    Volume(resolve(runConfig.exeMount.second)),
                                    AccessMode.ro
                                ),
                                Bind(tempDirectory.absolutePathString(), Volume(containerPathPrefix), AccessMode.rw),
                                Bind(stdin.absolutePathString(), Volume(resolve("stdin")), AccessMode.ro),
                            )
                            .withAutoRemove(!runConfig.debug)
                    )
                    .withEnv(runConfig.kojEnv.asList())
                    .withNetworkDisabled(true)
                    .withName("${runConfig.namespace}-execute")
                    .withWorkingDir(containerPathPrefix)
                    .exec()
                dockerClient.startContainerCmd(createResult.id).exec()
                log.debug { "container started:${createResult.id} with name:${runConfig.namespace}-compile" }
                val exitCode = try {
                    val waitResult = dockerClient.waitContainerCmd(createResult.id).start()
                    waitResult.awaitStatusCode()
                } catch (_: NotFoundException) {
                    0
                }
                log.debug { "container exited:${exitCode}" }
                val status = tryRead("load run status file failed") {
                    Status.load(tempDirectory.resolve("status"))
                }
                val stdout = tryRead("load run stdout file failed") {
                    Stdout(tempDirectory.resolve("stdout").readText())
                }
                val stderr = tryRead("load run stderr file failed") {
                    Stderr(tempDirectory.resolve("stderr").readText())
                }
                val log = tryRead("load run log file failed") {
                    Logging(tempDirectory.resolve("log").readText())
                }
                continuation.resume(KojDockerOutput(exitCode, status, stdout, stderr, log))
            } catch (e: Exception) {
                continuation.resumeWithException(e)
            }
        }
    }

    suspend fun compile(
        tempDirectory: TempDirectory,
        compileConfig: DockerSandboxCompileConfig
    ): KojDockerOutput {
        return suspendCancellableCoroutine { continuation ->
            try {
                val sourceFile =
                    tempDirectory.createFileWithContent(compileConfig.sourceFileName, compileConfig.sourceContent)
                log.debug { "create source file:${sourceFile} with content:${compileConfig.sourceContent}" }
                val createResult = dockerClient.createContainerCmd(compileConfig.image)
                    .withHostConfig(
                        HostConfig.newHostConfig()
                            .withBinds(
                                Bind(tempDirectory.absolutePathString(), Volume(containerPathPrefix), AccessMode.rw),
                                Bind(
                                    sourceFile.absolutePathString(),
                                    Volume(resolve(compileConfig.sourceFileName)),
                                    AccessMode.ro
                                ),
                            )
                            .withAutoRemove(!compileConfig.debug)
                    )
                    .withWorkingDir(containerPathPrefix)
                    .withEnv(compileConfig.kojEnv.asList())
                    .withNetworkDisabled(true)
                    .withName("${compileConfig.namespace}-compile")
                    .exec()
                dockerClient.startContainerCmd(createResult.id).exec()
                log.debug { "container started:${createResult.id} with name:${compileConfig.namespace}-compile" }
                val exitCode = try {
                    val waitResult = dockerClient.waitContainerCmd(createResult.id).start()
                    waitResult.awaitStatusCode()
                } catch (_: NotFoundException) {
                    0
                }
                log.debug { "container exited:${exitCode}" }
                val status = tryRead("load compile status file failed") {
                    Status.load(tempDirectory.resolve("status"))
                }
                val stdout = tryRead("load compile stdout file failed") {
                    Stdout(tempDirectory.resolve("stdout").readText())
                }
                val stderr = tryRead("load compile stderr file failed") {
                    Stderr(tempDirectory.resolve("stderr").readText())
                }
                val log = tryRead("load compile log file failed") {
                    Logging(tempDirectory.resolve("log").readText())
                }
                continuation.resume(KojDockerOutput(exitCode, status, stdout, stderr, log))
            } catch (e: Exception) {
                continuation.resumeWithException(e)
            }
        }
    }
}
