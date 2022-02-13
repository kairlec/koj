package com.kairlec.koj.engine

import com.kairlec.koj.engine.model.DockerInfo
import com.kairlec.koj.engine.model.DockerVersion
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import java.io.File


class HttpDockerClient(
    private val httpClient: HttpClient = HttpClient(OkHttp) {
        engine {
            config {
                socketFactory(UnixDomainSocketFactory(File("/var/run/docker.sock")))
            }
        }
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
    },
) : DockerClient {
    suspend fun info(): DockerInfo {
        return httpClient.get("/info").body()
    }

    suspend fun version(): DockerVersion {
        return httpClient.get("/version").body()
    }
}


suspend fun main() {
    println(HttpDockerClient().version())
    println(HttpDockerClient().info())
}