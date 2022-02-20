package com.kairlec.koj.engine

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.kairlec.koj.engine.extend.ContainerScope
import com.kairlec.koj.engine.model.DockerInfo
import com.kairlec.koj.engine.model.DockerVersion
import com.kairlec.koj.engine.serialzer.DockerModule
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.serialization.jackson.*
import kotlinx.coroutines.*
import java.io.Closeable
import java.io.File

internal lateinit var _json: ObjectMapper

internal data class HttpDockerClientContext constructor(
    val httpClient: HttpClient = HttpClient(OkHttp) {
        engine {
            config {
                socketFactory(UnixDomainSocketFactory(File("/var/run/docker.sock")))
            }
        }
        install(ContentNegotiation) {
            jackson {
                registerModule(DockerModule)
                disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                _json = this
            }
        }
        install(WebSockets) {
        }
    }
) : Closeable by httpClient {
    val json: ObjectMapper
        get() = _json


}

@DockerClientDSL
class HttpDockerClient internal constructor(
    internal val context: HttpDockerClientContext = HttpDockerClientContext()
) : Closeable by context {
    private val containerScope = ContainerScope(this)

    @DockerClientDSL
    suspend fun <T> Containers(block: suspend ContainerScope.() -> T): T {
        return block(containerScope)
    }

    companion object {
        @DockerClientDSL
        suspend operator fun invoke(block: suspend HttpDockerClient.() -> Unit) {
            HttpDockerClient().apply {
                block(this)
                close()
            }
        }
    }

    @DockerClientDSL
    suspend fun info(): DockerInfo {
        return context.httpClient.get("/info").body()
    }

    @DockerClientDSL
    suspend fun version(): DockerVersion {
        return context.httpClient.get("/version").body()
    }
}

suspend fun main() {
    HttpDockerClient {
//        println(version())
//        println(info())

        Containers {
            val containerId = create("nginx") {
                image = "ubuntu"
                entrypoint = listOf("/home/kairlec/KOJ/sample/main")
                attachStdin = true
                attachStdout = true
                attachStderr = true
                openStdin = true
                hostConfig {
                    autoRemove = true
                    binds = listOf(
                        "/home/kairlec/KOJ/sample:/home/kairlec/KOJ/sample"
                    )
                }
            }.id

            println("containerId: $containerId")

            start(containerId)
//            val containerId = "e25f38c701063dd6bb4996981ce9bca3ef00288041a6b1a9a5da1859b6ba922b"

            attach(containerId, {
                stream = true
                stdin = true
                stdout = true
                stderr = true
            }) {
                coroutineScope {
                    launch {
                        withContext(Dispatchers.IO) {
                            println("listening stdout")
                            stdout.bufferedReader().use {
                                while (isActive) {
                                    println("stdout: ${it.readLine()}")
                                }
                            }
                        }
                    }
                    launch {
                        withContext(Dispatchers.IO) {
                            println("listening stderr")
                            stderr.bufferedReader().use {
                                while (isActive) {
                                    println("stderr: ${it.readLine()}")
                                }
                            }
                        }
                    }
                    launch {
                        withContext(Dispatchers.IO) {
                            stdin.writer().use {
                                while (isActive) {
                                    it.write("nginx -v\n")
                                    it.flush()
                                    println("write 'nginx -v'")
                                    delay(1000)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
