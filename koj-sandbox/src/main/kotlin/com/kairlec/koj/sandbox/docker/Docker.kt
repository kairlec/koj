package com.kairlec.koj.sandbox.docker

import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.api.async.ResultCallback.Adapter
import com.github.dockerjava.api.model.*
import com.github.dockerjava.core.DefaultDockerClientConfig
import com.github.dockerjava.core.DockerClientImpl
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient
import com.github.dockerjava.transport.DockerHttpClient
import com.kairlec.koj.sandbox.Sandbox
import mu.KotlinLogging
import java.io.*
import java.time.Duration

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

    override fun run(runConfig: DockerSandboxRunConfig): InputStream {
        val result = dockerClient.createContainerCmd(runConfig.image)
            .withHostConfig(
                HostConfig.newHostConfig()
                    .withBinds(Bind(runConfig.workspace, Volume(runConfig.workspace), AccessMode.ro))
                    .withAutoRemove(true)
                // todo 限制资源
            )
            .withWorkingDir(runConfig.workspace)
            .withNetworkDisabled(true)
            .withName(runConfig.namespace)
            .withCmd(runConfig.executable)
            .withStdinOpen(true)
            .withAttachStderr(true)
            .withAttachStdout(true)
            .withStdInOnce(true)
            .withTty(true)
            .exec()
        dockerClient.startContainerCmd(result.id).exec()
        val stdin = PipedInputStream()
        val inOutputStream = PipedOutputStream(stdin)
        val pipedOutputStream = PipedOutputStream()
        val pipedInputStream = PipedInputStream(pipedOutputStream)
        val start = dockerClient
            .attachContainerCmd(result.id)
            .withStdIn(stdin)
            .withFollowStream(true)
            .withStdOut(true)
            .withStdErr(true)
            .exec(object : Adapter<Frame>() {
                override fun onNext(frame: Frame) {
                    pipedOutputStream.write(frame.payload)
                }

                override fun onComplete() {
                    super.onComplete()
                    pipedOutputStream.close()
                }
            })
        inOutputStream.write(runConfig.input.toByteArray())
        inOutputStream.close()
        start.awaitCompletion()
    }

    fun run(image: String) {
        dockerClient.createContainerCmd("gcc:11")
            .withWorkingDir("/usr/src/myapp")
            .withHostConfig(
                HostConfig.newHostConfig()
                    .withBinds(Bind("/usr/src/myapp", Volume("/usr/src/myapp"), AccessMode.ro))
                    .withAutoRemove(true)
                // todo 限制资源
            )
            .withCmd("gcc", "-o", "myapp", "myapp.c")
            .withNetworkDisabled(true)
            .exec()
    }

    override fun compile(compileConfig: DockerSandboxCompileConfig): InputStream {
        return PipedInputStream()
    }

}

fun main() {
    Docker.init(DockerSandboxInitConfig(listOf("ubuntu:20.04")))

    Docker.run(
        DockerSandboxRunConfig(
            "ubuntu:20.04",
            "/home/kairlec/KOJ/sample",
            listOf("/home/kairlec/KOJ/sample/main"),
            "kairlec",
            "WOOOOOOORLD\n\r\n"
        )
    ).readAllBytes().toString(Charsets.UTF_8).let {
        println(it)
    }
}