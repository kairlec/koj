package com.kairlec.koj.engine.extend

import com.google.gson.annotations.SerializedName
import com.kairlec.koj.engine.HttpDockerClient
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference

enum class Health {
    STARTING,
    HEALTHY,
    UNHEALTHY,
    NONE
}

enum class Isolation {
    DEFAULT,
    PROCESS,
    HYPERV,
}

enum class Status {
    CREATED,
    RESTARTING,
    RUNNING,
    REMOVING,
    PAUSED,
    EXITED,
    DEAD,
}

class ContainerScope(private val httpDockerClient: HttpDockerClient) {
    class ContainersListRequestFilterScope(
        var ancestor: List<String>? = null,
        var before: List<String>? = null,
        var expose: List<String>? = null,
        var exited: List<Int>? = null,
        var health: List<Health>? = null,
        var id: List<String>? = null,
        var isolation: List<Isolation>? = null,
        var isTask: List<Boolean>? = null,
        var label: List<String>? = null,
        var name: List<String>? = null,
        var network: List<String>? = null,
        var publish: List<String>? = null,
        var since: List<String>? = null,
        var status: List<Status>? = null,
        var volume: List<String>? = null,
    )

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
        val filters = request.filters?.let(httpDockerClient.context.json::toJson)
        val url = buildQuery("/containers/json") {
            request.all?.let { "all" on it }
            request.limit?.let { "limit" on it }
            request.size?.let { "size" on it }
            filters?.let { "filters" on it }
        }
        return httpDockerClient.context.httpClient.get(url).body()
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
