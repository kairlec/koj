package com.kairlec.koj.engine

import io.ktor.http.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference

@DockerClientDSL
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
