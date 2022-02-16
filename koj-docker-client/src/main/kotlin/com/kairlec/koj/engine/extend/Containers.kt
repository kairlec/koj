package com.kairlec.koj.engine.extend

import com.kairlec.koj.engine.DockerClientDSL
import com.kairlec.koj.engine.HttpDockerClient
import com.kairlec.koj.engine.buildQuery
import com.kairlec.koj.engine.model.ContainersListRequestBuilder
import com.kairlec.koj.engine.model.CreateContainerRequest
import com.kairlec.koj.engine.model.CreateContainerResponse
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

@DockerClientDSL
class ContainerScope(private val httpDockerClient: HttpDockerClient) {
    @DockerClientDSL
    suspend fun list(block: ContainersListRequestBuilder.() -> Unit): String {
        val request = ContainersListRequestBuilder().apply {
            block()
        }
        val filters = request.filters?.let(httpDockerClient.context.json::writeValueAsString)
        val url = buildQuery("/containers/json") {
            request.all?.let { "all" on it }
            request.limit?.let { "limit" on it }
            request.size?.let { "size" on it }
            filters?.let { "filters" on it }
        }
        return httpDockerClient.context.httpClient.get(url).body()
    }

    private val nameRegex = "^/?[a-zA-Z0-9][a-zA-Z0-9_.-]+\$".toRegex()

    @DockerClientDSL
    suspend fun create(
        name: String? = null,
        block: CreateContainerRequest.() -> Unit
    ): CreateContainerResponse {
        if (name != null) {
            require(name.matches(nameRegex)) { "Invalid container name" }
        }
        val url = buildQuery("/containers/create") {
            if (!name.isNullOrEmpty()) {
                "name" on name
            }
        }
        return httpDockerClient.context.httpClient.post(url) {
            contentType(ContentType.Application.Json)
            setBody(CreateContainerRequest().apply {
                block()
            })
        }.body()
    }

}

