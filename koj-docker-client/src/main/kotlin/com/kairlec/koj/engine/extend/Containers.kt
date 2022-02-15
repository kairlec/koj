@file:OptIn(ExperimentalSerializationApi::class)

package com.kairlec.koj.engine.extend

import com.kairlec.koj.engine.HttpDockerClient
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference

@Serializable
enum class Health {
    @SerialName("starting")
    STARTING,

    @SerialName("healthy")
    HEALTHY,

    @SerialName("unhealthy")
    UNHEALTHY,

    @SerialName("none")
    NONE
}

@Serializable
enum class Isolation {
    @SerialName("default")
    DEFAULT,

    @SerialName("process")
    PROCESS,

    @SerialName("hyperv")
    HYPERV,
}

@Serializable
enum class Status {
    @SerialName("created")
    CREATED,

    @SerialName("restarting")
    RESTARTING,

    @SerialName("running")
    RUNNING,

    @SerialName("removing")
    REMOVING,

    @SerialName("paused")
    PAUSED,

    @SerialName("exited")
    EXITED,

    @SerialName("dead")
    DEAD,
}

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
