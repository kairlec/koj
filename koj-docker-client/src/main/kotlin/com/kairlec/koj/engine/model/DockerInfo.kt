package com.kairlec.koj.engine.model


import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Instant

data class DockerInfo(
    @JsonProperty("Architecture")
    val architecture: String, // x86_64
    @JsonProperty("BridgeNfIp6tables")
    val bridgeNfIp6tables: Boolean, // true
    @JsonProperty("BridgeNfIptables")
    val bridgeNfIptables: Boolean, // true
    @JsonProperty("CPUSet")
    val cpuSet: Boolean, // true
    @JsonProperty("CPUShares")
    val cpuhares: Boolean, // true
    @JsonProperty("CgroupDriver")
    val cgroupDriver: String, // cgroupfs
    @JsonProperty("CgroupVersion")
    val cgroupVersion: String, // 1
    @JsonProperty("ClusterAdvertise")
    val clusterAdvertise: String? = null, // node5.corp.example.com:8000
    @JsonProperty("ClusterStore")
    val clusterStore: String? = null, // consul://consul.corp.example.com:8600/some/path
    @JsonProperty("ContainerdCommit")
    val containerdCommit: ContainerdCommit,
    @JsonProperty("Containers")
    val containers: Int, // 14
    @JsonProperty("ContainersPaused")
    val containersPaused: Int, // 1
    @JsonProperty("ContainersRunning")
    val containersRunning: Int, // 3
    @JsonProperty("ContainersStopped")
    val containersStopped: Int, // 10
    @JsonProperty("CpuCfsPeriod")
    val cpuCfsPeriod: Boolean, // true
    @JsonProperty("CpuCfsQuota")
    val cpuCfsQuota: Boolean, // true
    @JsonProperty("Debug")
    val debug: Boolean, // true
    @JsonProperty("DefaultAddressPools")
    val defaultAddressPools: List<DefaultAddressPool>? = null,
    @JsonProperty("DefaultRuntime")
    val defaultRuntime: String, // runc
    @JsonProperty("DockerRootDir")
    val dockerRootDir: String, // /var/lib/docker
    @JsonProperty("Driver")
    val driver: String, // overlay2
    @JsonProperty("DriverStatus")
    val driverStatus: List<List<String>>,
    @JsonProperty("ExperimentalBuild")
    val experimentalBuild: Boolean, // true
    @JsonProperty("GenericResources")
    val genericResources: List<GenericResource>?,
    @JsonProperty("HttpProxy")
    val httpProxy: String, // http://xxxxx:xxxxx@proxy.corp.example.com:8080
    @JsonProperty("HttpsProxy")
    val httpsProxy: String, // https://xxxxx:xxxxx@proxy.corp.example.com:4443
    @JsonProperty("ID")
    val id: String, // 7TRN:IPZB:QYBB:VPBQ:UMPP:KARE:6ZNR:XE6T:7EWV:PKF4:ZOJD:TPYS
    @JsonProperty("IPv4Forwarding")
    val iPv4Forwarding: Boolean, // true
    @JsonProperty("Images")
    val images: Int, // 508
    @JsonProperty("IndexServerAddress")
    val indexServerAddress: String, // https://index.docker.io/v1/
    @JsonProperty("InitBinary")
    val initBinary: String, // docker-init
    @JsonProperty("InitCommit")
    val initCommit: InitCommit,
    @JsonProperty("Isolation")
    val isolation: String, // default
    @JsonProperty("KernelMemory")
    val kernelMemory: Boolean, // true
    @JsonProperty("KernelVersion")
    val kernelVersion: String, // 4.9.38-moby
    @JsonProperty("Labels")
    val labels: List<String>,
    @JsonProperty("LiveRestoreEnabled")
    val liveRestoreEnabled: Boolean, // false
    @JsonProperty("LoggingDriver")
    val loggingDriver: String, // string
    @JsonProperty("MemTotal")
    val memTotal: Long, // 2095882240
    @JsonProperty("MemoryLimit")
    val memoryLimit: Boolean, // true
    @JsonProperty("NCPU")
    val nCPU: Int, // 4
    @JsonProperty("NEventsListener")
    val nEventsListener: Int, // 30
    @JsonProperty("NFd")
    val nFd: Int, // 64
    @JsonProperty("NGoroutines")
    val nGoroutines: Int, // 174
    @JsonProperty("Name")
    val name: String, // node5.corp.example.com
    @JsonProperty("NoProxy")
    val noProxy: String, // *.local, 169.254/16
    @JsonProperty("OSType")
    val oSType: String, // linux
    @JsonProperty("OSVersion")
    val oSVersion: String, // 16.04
    @JsonProperty("OomKillDisable")
    val oomKillDisable: Boolean, // true
    @JsonProperty("OperatingSystem")
    val operatingSystem: String, // Alpine Linux v3.5
    @JsonProperty("PidsLimit")
    val pidsLimit: Boolean, // true
    @JsonProperty("Plugins")
    val plugins: Plugins,
    @JsonProperty("ProductLicense")
    val productLicense: String? = null, // Community Engine
    @JsonProperty("RegistryConfig")
    val registryConfig: RegistryConfig,
    @JsonProperty("RuncCommit")
    val runcCommit: RuncCommit,
    @JsonProperty("Runtimes")
    val runtimes: Map<String, Runtime>,
    @JsonProperty("SecurityOptions")
    val securityOptions: List<String>,
    @JsonProperty("ServerVersion")
    val serverVersion: String, // 17.06.0-ce
    @JsonProperty("SwapLimit")
    val swapLimit: Boolean, // true
    @JsonProperty("Swarm")
    val swarm: Swarm,
    @JsonProperty("SystemTime")
    val systemTime: Instant, // 2017-08-08T20:28:29.06202363Z
    @JsonProperty("Warnings")
    val warnings: List<String>
) {
    data class ContainerdCommit(
        @JsonProperty("Expected")
        val expected: String, // 2d41c047c83e09a6d61d464906feb2a2f3c52aa4
        @JsonProperty("ID")
        val iD: String // cfb82a876ecc11b5ca0977d1733adbe58599088a
    )

    data class DefaultAddressPool(
        @JsonProperty("Base")
        val base: String, // 10.10.0.0/16
        @JsonProperty("Size")
        val size: String // 24
    )

    data class GenericResource(
        @JsonProperty("DiscreteResourceSpec")
        val discreteResourceSpec: DiscreteResourceSpec,
        @JsonProperty("NamedResourceSpec")
        val namedResourceSpec: NamedResourceSpec
    ) {
        data class DiscreteResourceSpec(
            @JsonProperty("Kind")
            val kind: String, // SSD
            @JsonProperty("Value")
            val value: Int // 3
        )

        data class NamedResourceSpec(
            @JsonProperty("Kind")
            val kind: String, // GPU
            @JsonProperty("Value")
            val value: String // UUID1
        )
    }

    data class InitCommit(
        @JsonProperty("Expected")
        val expected: String, // 2d41c047c83e09a6d61d464906feb2a2f3c52aa4
        @JsonProperty("ID")
        val id: String // cfb82a876ecc11b5ca0977d1733adbe58599088a
    )

    data class Plugins(
        @JsonProperty("Authorization")
        val authorization: List<String>?,
        @JsonProperty("Log")
        val log: List<String>,
        @JsonProperty("Network")
        val network: List<String>,
        @JsonProperty("Volume")
        val volume: List<String>
    )

    data class RegistryConfig(
        @JsonProperty("AllowNondistributableArtifactsCIDRs")
        val allowNondistributableArtifactsCIDRs: List<String>,
        @JsonProperty("AllowNondistributableArtifactsHostnames")
        val allowNondistributableArtifactsHostnames: List<String>,
        @JsonProperty("IndexConfigs")
        val indexConfigs: Map<String, IndexConfig>,
        @JsonProperty("InsecureRegistryCIDRs")
        val insecureRegistryCIDRs: List<String>,
        @JsonProperty("Mirrors")
        val mirrors: List<String>
    ) {
        data class IndexConfig(
            @JsonProperty("Mirrors")
            val mirrors: List<String>,
            @JsonProperty("Name")
            val name: String, // docker.io
            @JsonProperty("Official")
            val official: Boolean, // true
            @JsonProperty("Secure")
            val secure: Boolean // true
        )
    }

    data class RuncCommit(
        @JsonProperty("Expected")
        val expected: String, // 2d41c047c83e09a6d61d464906feb2a2f3c52aa4
        @JsonProperty("ID")
        val iD: String // cfb82a876ecc11b5ca0977d1733adbe58599088a
    )

    data class Runtime(
        @JsonProperty("path")
        val path: String, // /usr/local/bin/my-oci-runtime
        @JsonProperty("runtimeArgs")
        val runtimeArgs: List<String>? = null
    )

    data class Swarm(
        @JsonProperty("Cluster")
        val cluster: Cluster? = null,
        @JsonProperty("ControlAvailable")
        val controlAvailable: Boolean, // true
        @JsonProperty("Error")
        val error: String,
        @JsonProperty("LocalNodeState")
        val localNodeState: String, // active
        @JsonProperty("Managers")
        val managers: Int? = null, // 3
        @JsonProperty("NodeAddr")
        val nodeAddr: String, // 10.0.0.46
        @JsonProperty("NodeID")
        val nodeID: String, // k67qz4598weg5unwwffg6z1m1
        @JsonProperty("Nodes")
        val nodes: Int? = null, // 4
        @JsonProperty("RemoteManagers")
        val remoteManagers: List<RemoteManager>?
    ) {
        data class Cluster(
            @JsonProperty("CreatedAt")
            val createdAt: String, // 2016-08-18T10:44:24.496525531Z
            @JsonProperty("DataPathPort")
            val dataPathPort: Int, // 4789
            @JsonProperty("DefaultAddrPool")
            val defaultAddrPool: List<List<String>>,
            @JsonProperty("ID")
            val iD: String, // abajmipo7b4xz5ip2nrla6b11
            @JsonProperty("RootRotationInProgress")
            val rootRotationInProgress: Boolean, // false
            @JsonProperty("Spec")
            val spec: Spec,
            @JsonProperty("SubnetSize")
            val subnetSize: Int, // 24
            @JsonProperty("TLSInfo")
            val tLSInfo: TLSInfo,
            @JsonProperty("UpdatedAt")
            val updatedAt: Instant, // 2017-08-09T07:09:37.632105588Z
            @JsonProperty("Version")
            val version: Version
        ) {
            data class Spec(
                @JsonProperty("CAConfig")
                val caConfig: CAConfig,
                @JsonProperty("Dispatcher")
                val dispatcher: Dispatcher,
                @JsonProperty("EncryptionConfig")
                val encryptionConfig: EncryptionConfig,
                @JsonProperty("Labels")
                val labels: Map<String, String>,
                @JsonProperty("Name")
                val name: String, // default
                @JsonProperty("Orchestration")
                val orchestration: Orchestration,
                @JsonProperty("Raft")
                val raft: Raft,
                @JsonProperty("TaskDefaults")
                val taskDefaults: TaskDefaults
            ) {
                data class CAConfig(
                    @JsonProperty("ExternalCAs")
                    val externalCAs: List<ExternalCA>,
                    @JsonProperty("ForceRotate")
                    val forceRotate: Int, // 0
                    @JsonProperty("NodeCertExpiry")
                    val nodeCertExpiry: Long, // 7776000000000000
                    @JsonProperty("SigningCACert")
                    val signingCACert: String, // string
                    @JsonProperty("SigningCAKey")
                    val signingCAKey: String // string
                ) {
                    data class ExternalCA(
                        @JsonProperty("CACert")
                        val caCert: String, // string
                        @JsonProperty("Options")
                        val options: Map<String, String>,
                        @JsonProperty("Protocol")
                        val protocol: String, // cfssl
                        @JsonProperty("URL")
                        val url: String // string
                    )
                }

                data class Dispatcher(
                    @JsonProperty("HeartbeatPeriod")
                    val heartbeatPeriod: Long // 5000000000
                )

                data class EncryptionConfig(
                    @JsonProperty("AutoLockManagers")
                    val autoLockManagers: Boolean // false
                )

                data class Orchestration(
                    @JsonProperty("TaskHistoryRetentionLimit")
                    val taskHistoryRetentionLimit: Int // 10
                )

                data class Raft(
                    @JsonProperty("ElectionTick")
                    val electionTick: Int, // 3
                    @JsonProperty("HeartbeatTick")
                    val heartbeatTick: Int, // 1
                    @JsonProperty("KeepOldSnapshots")
                    val keepOldSnapshots: Int, // 0
                    @JsonProperty("LogEntriesForSlowFollowers")
                    val logEntriesForSlowFollowers: Int, // 500
                    @JsonProperty("SnapshotInterval")
                    val snapshotInterval: Int // 10000
                )

                data class TaskDefaults(
                    @JsonProperty("LogDriver")
                    val logDriver: LogDriver
                ) {
                    data class LogDriver(
                        @JsonProperty("Name")
                        val name: String, // json-file
                        @JsonProperty("Options")
                        val options: Map<String, String>
                    )
                }
            }

            data class TLSInfo(
                @JsonProperty("CertIssuerPublicKey")
                val certIssuerPublicKey: String, // MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEmT9XIw9h1qoNclv9VeHmf/Vi6/uI2vFXdBveXTpcPjqx6i9wNazchk1XWV/dKTKvSh9xyGKmiIeRcE4OiMnJ1A==
                @JsonProperty("CertIssuerSubject")
                val certIssuerSubject: String, // MBMxETAPBgNVBAMTCHN3YXJtLWNh
                @JsonProperty("TrustRoot")
                val trustRoot: String // -----BEGIN CERTIFICATE-----MIIBajCCARCgAwIBAgIUbYqrLSOSQHoxD8CwG6Bi2PJi9c8wCgYIKoZIzj0EAwIwEzERMA8GA1UEAxMIc3dhcm0tY2EwHhcNMTcwNDI0MjE0MzAwWhcNMzcwNDE5MjE0MzAwWjATMREwDwYDVQQDEwhzd2FybS1jYTBZMBMGByqGSM49AgEGCCqGSM49AwEHA0IABJk/VyMPYdaqDXJb/VXh5n/1Yuv7iNrxV3Qb3l06XD46seovcDWs3IZNV1lf3Skyr0ofcchipoiHkXBODojJydSjQjBAMA4GA1UdDwEB/wQEAwIBBjAPBgNVHRMBAf8EBTADAQH/MB0GA1UdDgQWBBRUXxuRcnFjDfR/RIAUQab8ZV/n4jAKBggqhkjOPQQDAgNIADBFAiAy+JTe6Uc3KyLCMiqGl2GyWGQqQDEcO3/YG36x7om65AIhAJvzpxv6zFeVEkAEEkqIYi0omA9+CjanB/6Bz4n1uw8H-----END CERTIFICATE-----
            )

            data class Version(
                @JsonProperty("Index")
                val index: Int // 373531
            )
        }

        data class RemoteManager(
            @JsonProperty("Addr")
            val addr: String, // 10.0.0.158:2377
            @JsonProperty("NodeID")
            val nodeID: String // 71izy0goik036k48jg985xnds
        )
    }
}