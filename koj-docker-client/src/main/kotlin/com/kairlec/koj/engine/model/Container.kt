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
    var workingDir: String? = null, // string

    @JsonProperty("HostConfig")
    var hostConfig: HostConfig? = null
) {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    data class HostConfig(
        @JsonProperty("AutoRemove")
        var autoRemove: Boolean? = null, // true
        @JsonProperty("Binds")
        var binds: List<String>? = null,
        @JsonProperty("BlkioDeviceReadBps")
        var blkioDeviceReadBps: List<Any>? = null,
        @JsonProperty("BlkioDeviceReadIOps")
        var blkioDeviceReadIOps: List<Any>? = null,
        @JsonProperty("BlkioDeviceWriteBps")
        var blkioDeviceWriteBps: List<Any>? = null,
        @JsonProperty("BlkioDeviceWriteIOps")
        var blkioDeviceWriteIOps: List<Any>? = null,
        @JsonProperty("BlkioWeight")
        var blkioWeight: Int? = null, // 300
        @JsonProperty("BlkioWeightDevice")
        var blkioWeightDevice: List<Any>? = null,
        @JsonProperty("CapAdd")
        var capAdd: List<Any>? = null,
        @JsonProperty("CapDrop")
        var capDrop: List<Any>? = null,
        @JsonProperty("CgroupParent")
        var cgroupParent: String? = null,
        @JsonProperty("CpuPercent")
        var cpuPercent: Int? = null, // 80
        @JsonProperty("CpuPeriod")
        var cpuPeriod: Int? = null, // 100000
        @JsonProperty("CpuQuota")
        var cpuQuota: Int? = null, // 50000
        @JsonProperty("CpuRealtimePeriod")
        var cpuRealtimePeriod: Int? = null, // 1000000
        @JsonProperty("CpuRealtimeRuntime")
        var cpuRealtimeRuntime: Int? = null, // 10000
        @JsonProperty("CpuShares")
        var cpuShares: Int? = null, // 512
        @JsonProperty("CpusetCpus")
        var cpusetCpus: String? = null, // 0,1
        @JsonProperty("CpusetMems")
        var cpusetMems: String? = null, // 0,1
        @JsonProperty("DeviceRequests")
        var deviceRequests: List<Any>? = null,
        @JsonProperty("Devices")
        var devices: List<Any>? = null,
        @JsonProperty("Dns")
        var dns: List<Any>? = null,
        @JsonProperty("DnsOptions")
        var dnsOptions: List<Any>? = null,
        @JsonProperty("DnsSearch")
        var dnsSearch: List<Any>? = null,
        @JsonProperty("GroupAdd")
        var groupAdd: List<Any>? = null,
        @JsonProperty("KernelMemory")
        var kernelMemory: Int? = null, // 0
        @JsonProperty("Links")
        var links: List<Any>? = null,
        @JsonProperty("MaximumIOBps")
        var maximumIOBps: Int? = null, // 0
        @JsonProperty("MaximumIOps")
        var maximumIOps: Int? = null, // 0
        @JsonProperty("Memory")
        var memory: Int? = null, // 0
        @JsonProperty("MemoryReservation")
        var memoryReservation: Int? = null, // 0
        @JsonProperty("MemorySwap")
        var memorySwap: Int? = null, // 0
        @JsonProperty("MemorySwappiness")
        var memorySwappiness: Int? = null, // 60
        @JsonProperty("NanoCpus")
        var nanoCpus: Int? = null, // 500000
        @JsonProperty("NetworkMode")
        var networkMode: String? = null, // bridge
        @JsonProperty("OomKillDisable")
        var oomKillDisable: Boolean? = null, // false
        @JsonProperty("OomScoreAdj")
        var oomScoreAdj: Int? = null, // 500
        @JsonProperty("PidMode")
        var pidMode: String? = null,
        @JsonProperty("PidsLimit")
        var pidsLimit: Int? = null, // 0
        @JsonProperty("Privileged")
        var privileged: Boolean? = null, // false
        @JsonProperty("PublishAllPorts")
        var publishAllPorts: Boolean? = null, // false
        @JsonProperty("ReadonlyRootfs")
        var readonlyRootfs: Boolean? = null, // false
        @JsonProperty("RestartPolicy")
        var restartPolicy: RestartPolicy? = null,
        @JsonProperty("SecurityOpt")
        var securityOpt: List<Any>? = null,
        @JsonProperty("ShmSize")
        var shmSize: Int? = null, // 67108864
        @JsonProperty("StorageOpt")
        var storageOpt: Map<String, String>? = null,
        @JsonProperty("Ulimits")
        var ulimits: List<Any>? = null,
        @JsonProperty("VolumeDriver")
        var volumeDriver: String? = null,
        @JsonProperty("VolumesFrom")
        var volumesFrom: List<Any>? = null,
    ) {
        @JsonInclude(JsonInclude.Include.NON_NULL)
        sealed class RestartPolicy {
            @get:JsonProperty("Name")
            abstract val name: String

            @get:JsonProperty("MaximumRetryCount")
            open val maximumRetryCount: Int? = null

            object Never : RestartPolicy() {
                override val name: String = "no"
            }

            object Always : RestartPolicy() {
                override val name: String = "always"
            }

            object UnlessStopped : RestartPolicy() {
                override val name: String = "unless-stopped"
            }

            class OnFailure(override val maximumRetryCount: Int) : RestartPolicy() {
                override val name: String
                    get() = "on-failure"
            }
        }
    }

    fun hostConfig(block: HostConfig.() -> Unit) {
        hostConfig = HostConfig().apply(block)
    }

    @JsonSerialize(using = ExposedPorts.ExposedPortsSerializer::class)
    data class ExposedPorts(
        private val list: List<ExposedPort>
    ) : List<ExposedPorts.ExposedPort> by list {
        data class ExposedPort(
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
    @JsonInclude(JsonInclude.Include.NON_NULL)
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
) {
    @DockerClientDSL
    fun filters(block: ContainersListRequestFilterScope.() -> Unit) {
        filters = ContainersListRequestFilterScope().apply(block)
    }
}
