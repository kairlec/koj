package com.kairlec.koj.engine.model

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import com.kairlec.koj.engine.DockerClientDSL

@JsonInclude(JsonInclude.Include.NON_NULL)
data class CreateContainerRequest(
    @JsonProperty("ArgsEscaped")
    var argsEscaped: Boolean? = null, // true

    @JsonProperty("AttachStderr")
    var attachStderr: Boolean? = null, // true

    @JsonProperty("AttachStdin")
    var attachStdin: Boolean? = null, // false

    @JsonProperty("AttachStdout")
    var attachStdout: Boolean? = null, // true

    @JsonProperty("Cmd")
    var cmd: List<String>? = null,

    @JsonProperty("Domainname")
    var domainname: String? = null, // string

    @JsonProperty("Entrypoint")
    var entrypoint: List<String>? = null,

    @JsonProperty("Env")
    var env: List<String>? = null,

    @JsonProperty("ExposedPorts")
    var exposedPorts: ExposedPorts? = null,

    @JsonProperty("Healthcheck")
    var healthcheck: HealthCheck? = null,

    @JsonProperty("Hostname")
    var hostname: String? = null, // string

    @JsonProperty("Image")
    var image: String? = null, // string

    @JsonProperty("Labels")
    var labels: Map<String, String>? = null,

    @JsonProperty("MacAddress")
    var macAddress: String? = null, // string

    @JsonProperty("NetworkDisabled")
    var networkDisabled: Boolean? = null, // true

    @JsonProperty("OnBuild")
    var onBuild: List<String>? = null,

    @JsonProperty("OpenStdin")
    var openStdin: Boolean? = null, // false

    @JsonProperty("Shell")
    var shell: List<String>? = null,

    @JsonProperty("StdinOnce")
    var stdinOnce: Boolean? = null, // false

    @JsonProperty("StopSignal")
    var stopSignal: String? = null, // SIGTERM

    @JsonProperty("StopTimeout")
    var stopTimeout: Int? = null, // 10

    @JsonProperty("Tty")
    var tty: Boolean? = null, // false

    @JsonProperty("User")
    var user: String? = null, // string

    @JsonProperty("Volumes")
    var volumes: Volumes? = null,

    @JsonProperty("WorkingDir")
    var workingDir: String? = null // string
) {
    @JsonSerialize(using = ExposedPorts.ExposedPortsSerializer::class)
    data class ExposedPorts(
        private val list: List<ExposedPort>
    ) : List<ExposedPorts.ExposedPort> by list {
        class ExposedPort(
            val port: Int,
            val type: PortType
        ) {
            enum class PortType {
                @JsonProperty("tcp")
                TCP,

                @JsonProperty("udp")
                UDP,

                @JsonProperty("sctp")
                SCTP
            }
        }

        companion object ExposedPortsSerializer : StdSerializer<ExposedPorts>(ExposedPorts::class.java) {
            override fun serialize(value: ExposedPorts, gen: JsonGenerator, provider: SerializerProvider) {
                gen.writeStartObject()
                value.list.forEach {
                    gen.writeFieldName(it.port.toString() + "/" + it.type.name.lowercase())
                    gen.writeStartObject()
                    gen.writeEndObject()
                }
                gen.writeEndObject()
            }
        }
    }

    data class HealthCheck(
        @JsonProperty("Interval")
        var interval: Int? = null, // 0

        @JsonProperty("Retries")
        var retries: Int? = null, // 0

        @JsonProperty("StartPeriod")
        var startPeriod: Int? = null, // 0

        @JsonProperty("Test")
        var test: List<String>? = null,

        @JsonProperty("Timeout")
        var timeout: Int? = null // 0
    )

    @JsonSerialize(using = Volumes.VolumesSerializer::class)
    data class Volumes(
        private val list: List<String>
    ) : List<String> by list {
        companion object VolumesSerializer : StdSerializer<Volumes>(Volumes::class.java) {
            override fun serialize(value: Volumes, gen: JsonGenerator, provider: SerializerProvider) {
                gen.writeStartObject()
                value.list.forEach {
                    gen.writeFieldName(it)
                    gen.writeStartObject()
                    gen.writeEndObject()
                }
                gen.writeEndObject()
            }
        }
    }
}

data class CreateContainerResponse(
    @JsonProperty("Id")
    val id: String, // e90e34656806
    @JsonProperty("Warnings")
    val warnings: List<String>
)


enum class Health {
    @JsonProperty("starting")
    STARTING,

    @JsonProperty("healthy")
    HEALTHY,

    @JsonProperty("unhealthy")
    UNHEALTHY,

    @JsonProperty("none")
    NONE
}

enum class Isolation {
    @JsonProperty("default")
    DEFAULT,

    @JsonProperty("process")
    PROCESS,

    @JsonProperty("hyperv")
    HYPERV,
}

enum class Status {
    @JsonProperty("created")
    CREATED,

    @JsonProperty("restarting")
    RESTARTING,

    @JsonProperty("running")
    RUNNING,

    @JsonProperty("removing")
    REMOVING,

    @JsonProperty("paused")
    PAUSED,

    @JsonProperty("exited")
    EXITED,

    @JsonProperty("dead")
    DEAD,
}

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ContainersListRequestFilterScope(
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

@DockerClientDSL
class ContainersListRequestBuilder(
    var all: Boolean? = null,
    var limit: Int? = null,
    var size: Boolean? = null,
    internal var filters: ContainersListRequestFilterScope? = null,
){
    @DockerClientDSL
    fun filters(block: ContainersListRequestFilterScope.() -> Unit) {
        filters = ContainersListRequestFilterScope().apply(block)
    }
}
