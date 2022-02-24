package com.kairlec.koj.sandbox.docker

import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.api.model.AccessMode
import com.github.dockerjava.api.model.Bind
import com.github.dockerjava.api.model.HostConfig
import com.github.dockerjava.api.model.Volume
import com.github.dockerjava.core.DefaultDockerClientConfig
import com.github.dockerjava.core.DockerClientImpl
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient
import com.kairlec.koj.sandbox.Sandbox
import mu.KotlinLogging
import java.io.*
import java.time.Duration

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

    override fun run(runConfig: DockerSandboxRunConfig): String {
        return TempDirectory.create(runConfig.workspace).use {
            val createResult = dockerClient.createContainerCmd(runConfig.image)
                .withHostConfig(
                    HostConfig.newHostConfig()
                        .withBinds(Bind(it.absolutePath, Volume("/tmp/output"), AccessMode.rw))
                        .withAutoRemove(true)
                )
                .withWorkingDir(runConfig.workspace)
                .withNetworkDisabled(true)
                .withName(runConfig.namespace)
                .withCmd(runConfig.executable)
                .withStdinOpen(true)
                .withAttachStdin(true)
                .withStdInOnce(true)
                .withTty(true)
                .exec()
            dockerClient.startContainerCmd(createResult.id).exec()
            val attachResult = dockerClient
                .attachContainerCmd(createResult.id)
                .withStdIn(ByteArrayInputStream(runConfig.input.toByteArray()))
                .withFollowStream(true)
                .withStdErr(true)
                .start()
            attachResult.awaitCompletion()
            it.readText()
//        val (pipedInputStream, pipedOutputStream) = makePiped()
//        val logListenResult = dockerClient.logContainerCmd(createResult.id)
//            .withStdOut(true)
//            .withFollowStream(true)
//            .exec(object : Adapter<Frame>() {
//                override fun onNext(frame: Frame) {
//                    pipedOutputStream.write(frame.payload)
//                    pipedOutputStream.flush()
//                }
//
//                override fun onComplete() {
//                    super.onComplete()
//                    pipedOutputStream.close()
//                }
//            })
//        logListenResult.awaitStarted()
//        val attachResult = dockerClient
//            .attachContainerCmd(createResult.id)
//            .withStdIn(ByteArrayInputStream(runConfig.input.toByteArray()))
//            .withFollowStream(true)
//            .withStdErr(true)
//            .start()
//        block(pipedInputStream)
//        attachResult.awaitCompletion()
        }
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
            image = "ubuntu:20.04",
            workspace = "/home/kairlec/KOJ/sample",
            executable = listOf("/home/kairlec/KOJ/sample/main"),
            namespace = "kairlec",
            input = "WOOOOOOORLD\n"
        )
    ) {
        it.readAllBytes().toString(Charsets.UTF_8).let {
            println("result=[${it}]")
        }
    }
}