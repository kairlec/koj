package com.kairlec.koj.sandbox.docker

import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.api.model.AccessMode
import com.github.dockerjava.api.model.Bind
import com.github.dockerjava.api.model.HostConfig
import com.github.dockerjava.api.model.Volume
import com.github.dockerjava.core.DefaultDockerClientConfig
import com.github.dockerjava.core.DockerClientImpl
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient
import com.kairlec.koj.sandbox.*
import mu.KotlinLogging
import java.io.InputStream
import java.io.OutputStream
import java.io.PipedInputStream
import java.io.PipedOutputStream
import java.time.Duration
import kotlin.io.path.absolutePathString
import kotlin.io.path.readText

fun makePiped(): Pair<InputStream, OutputStream> {
    val pipedOutputStream = PipedOutputStream()
    val pipedInputStream = PipedInputStream(pipedOutputStream)
    return Pair(pipedInputStream, pipedOutputStream)
}

object Docker : Sandbox<DockerSandboxInitConfig, DockerSandboxRunConfig, DockerSandboxCompileConfig> {
    private val log = KotlinLogging.logger {}
    private lateinit var dockerClient: DockerClient


    override fun init(initConfig: DockerSandboxInitConfig) {
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

        initConfig.prepareImages.forEach {
            val imagePullResult = dockerClient.pullImageCmd(it).start()
            try {
                imagePullResult.awaitCompletion()
            } catch (e: Exception) {
                log.error(e) { "prepare image for $it failed" }
                throw DockerSandboxInitImageFailedException(it, e)
            }
        }
    }

    override fun run(runConfig: DockerSandboxRunConfig): SandboxOutput {
        return TempDirectory.create(runConfig.namespace).use {
            val stdin = it.createFileWithContent("stdin", runConfig.input)
            val createResult = dockerClient.createContainerCmd(runConfig.image)
                .withHostConfig(
                    HostConfig.newHostConfig()
                        .withBinds(
                            Bind(runConfig.exeMount.first, Volume(runConfig.exeMount.second), AccessMode.ro),
                            Bind(it.absolutePathString(), Volume("/tmp/koj"), AccessMode.rw),
                            Bind(stdin.absolutePathString(), Volume("/tmp/koj/stdin"), AccessMode.rw),
                        )
                        .withAutoRemove(true)
                )
                .withEnv(runConfig.kojEnv.asList())
                .withNetworkDisabled(true)
                .withName(runConfig.namespace)
                .exec()
            dockerClient.startContainerCmd(createResult.id).exec()
            Thread.sleep(3000)
            val status = Status.load(it.resolve("status"))
            val stdout = Stdout(it.resolve("stdout").readText())
            val log = Logging(it.resolve("log").readText())
            SandboxOutput(status, stdout, log)
        }
    }

    override fun compile(compileConfig: DockerSandboxCompileConfig): String {
        return ""
    }

}

fun main() {
    Docker.init(DockerSandboxInitConfig(emptyList()))

    val output = Docker.run(
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