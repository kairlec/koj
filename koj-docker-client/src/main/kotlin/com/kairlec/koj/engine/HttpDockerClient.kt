package com.kairlec.koj.engine

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import com.kairlec.koj.engine.extend.ContainerScope
import com.kairlec.koj.engine.model.DockerInfo
import com.kairlec.koj.engine.model.DockerVersion
import com.kairlec.koj.engine.serialzer.InstantTypeAdapter
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.serialization.gson.*
import java.io.File
import java.time.Instant

val tp = object : TypeAdapterFactory {
    override fun <T : Any> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T>? {
        val raw = type.rawType
        if (!raw.isEnum) {
            return null
        }
        return object : TypeAdapter<T>() {
            val enumConstants = raw.enumConstants as Array<Enum<*>>
            override fun write(out: JsonWriter, value: T) {
                out.value((value as Enum<*>).name.lowercase())
            }

            override fun read(`in`: JsonReader): T {
                return `in`.nextString().let { str ->
                    enumConstants.first { it.name.equals(str, true) } as T
                }
            }
        }
    }
}

internal data class HttpDockerClientContext constructor(
    val json: Gson = GsonBuilder()
        .registerTypeAdapterFactory(tp)
        .registerTypeAdapter(Instant::class.java, InstantTypeAdapter)
        .create(),
    val httpClient: HttpClient = HttpClient(OkHttp) {
        engine {
            config {
                socketFactory(UnixDomainSocketFactory(File("/var/run/docker.sock")))
            }
        }
        install(ContentNegotiation) {
            gson {
                registerTypeAdapterFactory(tp)
                registerTypeAdapter(Instant::class.java, InstantTypeAdapter)
            }
        }
    }
)

class HttpDockerClient internal constructor(
    internal val context: HttpDockerClientContext = HttpDockerClientContext()
) : DockerClient {
    private val httpClient get() = context.httpClient
    suspend fun info(): DockerInfo {
        return httpClient.get("/info").body()
    }

    suspend fun version(): DockerVersion {
        return httpClient.get("/version").body()
    }

    private val containerScope = ContainerScope(this)

    suspend fun <T> Containers(block: suspend ContainerScope.() -> T): T {
        return block(containerScope)
    }

    companion object {
        suspend operator fun invoke(block: suspend HttpDockerClient.() -> Unit) {
            block(HttpDockerClient())
        }
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
