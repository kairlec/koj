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
import io.ktor.client.request.*
import io.ktor.serialization.jackson.*
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
    }
) {
    val json: ObjectMapper
        get() = _json
}

@DockerClientDSL
class HttpDockerClient internal constructor(
    internal val context: HttpDockerClientContext = HttpDockerClientContext()
) {
    private val containerScope = ContainerScope(this)

    @DockerClientDSL
    suspend fun <T> Containers(block: suspend ContainerScope.() -> T): T {
        return block(containerScope)
    }

    companion object {
        @DockerClientDSL
        suspend operator fun invoke(block: suspend HttpDockerClient.() -> Unit) {
            block(HttpDockerClient())
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
        println(version())
        println(info())

        Containers {
            println(list {
                all = true
            })
        }
    }
}
