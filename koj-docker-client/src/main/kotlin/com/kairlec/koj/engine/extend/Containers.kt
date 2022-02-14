@file:OptIn(ExperimentalSerializationApi::class)

package com.kairlec.koj.engine.extend

import com.kairlec.koj.engine.HttpDockerClient
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference

class ContainerScope(private val httpDockerClient: HttpDockerClient) {
    @Serializable
    class ContainersListRequestFilterScope(
        @EncodeDefault(EncodeDefault.Mode.NEVER)
        var ancestor: List<String>? = null,
        @EncodeDefault(EncodeDefault.Mode.NEVER)
        var before: List<String>? = null,
        @EncodeDefault(EncodeDefault.Mode.NEVER)
        var expose: List<String>? = null,
        @EncodeDefault(EncodeDefault.Mode.NEVER)
        var exited: List<Int>? = null,
        @EncodeDefault(EncodeDefault.Mode.NEVER)
        var health: List<Health>? = null,
        @EncodeDefault(EncodeDefault.Mode.NEVER)
        var id: List<String>? = null,
        @EncodeDefault(EncodeDefault.Mode.NEVER)
        var isolation: List<Isolation>? = null,
        @EncodeDefault(EncodeDefault.Mode.NEVER)
        var isTask: List<Boolean>? = null,
        @EncodeDefault(EncodeDefault.Mode.NEVER)
        var label: List<String>? = null,
        @EncodeDefault(EncodeDefault.Mode.NEVER)
        var name: List<String>? = null,
        @EncodeDefault(EncodeDefault.Mode.NEVER)
        var network: List<String>? = null,
        @EncodeDefault(EncodeDefault.Mode.NEVER)
        var publish: List<String>? = null,
        @EncodeDefault(EncodeDefault.Mode.NEVER)
        var since: List<String>? = null,
        @EncodeDefault(EncodeDefault.Mode.NEVER)
        var status: List<Status>? = null,
        @EncodeDefault(EncodeDefault.Mode.NEVER)
        var volume: List<String>? = null,
    )

    enum class Health(private val value: String) {
        STARTING("starting"),
        HEALTHY("healthy"),
        UNHEALTHY("unhealthy"),
        NONE("none")
    }

    enum class Isolation(private val value: String) {
        DEFAULT("default"),
        PROCESS("process"),
        HYPERV("hyperv"),
    }

    enum class Status(private val value: String) {
        CREATED("created"),
        RESTARTING("restarting"),
        RUNNING("running"),
        REMOVING("removing"),
        PAUSED("paused"),
        EXITED("exited"),
        DEAD("dead"),
    }

    class ContainersListRequestBuilder(
        var all: Boolean? = null,
        var limit: Int? = null,
        var size: Boolean? = null,
        internal var filters: ContainersListRequestFilterScope? = null,
    ) {
        fun filters(block: ContainersListRequestFilterScope.() -> Unit) {
            filters = ContainersListRequestFilterScope().apply(block)
        }
    }

    suspend fun list(block: ContainersListRequestBuilder.() -> Unit): String {
        val request = ContainersListRequestBuilder().apply(block)
        val filters = request.filters?.let(httpDockerClient.context.json::encodeToString)
        val url = buildQuery("/containers/json") {
            request.all?.let { "all" on it }
            request.limit?.let { "all" on it }
            request.size?.let { "all" on it }
            filters?.let { "filters" on it }
        }
        return httpDockerClient.httpClient.get(url).body()
    }
}

class BuildQueryScope(internal val pairs: MutableList<Pair<String, String?>>) {
    infix fun String.on(value: Any) {
        pairs += this.encodeURLQueryComponent() to value.toString().encodeURLQueryComponent()
    }

    fun append(key: String) {
        pairs += key.encodeURLQueryComponent() to null
    }
}

@OptIn(ExperimentalTypeInference::class, ExperimentalContracts::class)
fun buildQuery(base: String, @BuilderInference builderAction: BuildQueryScope.() -> Unit): String {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    val query =
        BuildQueryScope(mutableListOf()).apply(builderAction).pairs.joinToString("&") {
            if (it.second != null) {
                "${it.first}=${it.second}"
            } else {
                it.first
            }
        }
    return if (query.isEmpty()) {
        base
    } else {
        "$base?$query"
    }
}

suspend fun main() {
    HttpDockerClient().Containers {
        list {
            all = true
        }
    }.apply {
        println(this)
    }
}