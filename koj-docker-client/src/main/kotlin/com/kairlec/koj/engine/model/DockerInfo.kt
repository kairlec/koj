package com.kairlec.koj.engine.model


import com.kairlec.koj.engine.serialzer.InstantSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class DockerInfo(
    @SerialName("Architecture")
    val architecture: String, // x86_64
    @SerialName("BridgeNfIp6tables")
    val bridgeNfIp6tables: Boolean, // true
    @SerialName("BridgeNfIptables")
    val bridgeNfIptables: Boolean, // true
    @SerialName("CPUSet")
    val cPUSet: Boolean, // true
    @SerialName("CPUShares")
    val cPUShares: Boolean, // true
    @SerialName("CgroupDriver")
    val cgroupDriver: String, // cgroupfs
    @SerialName("CgroupVersion")
    val cgroupVersion: String, // 1
    @SerialName("ClusterAdvertise")
    val clusterAdvertise: String? = null, // node5.corp.example.com:8000
    @SerialName("ClusterStore")
    val clusterStore: String? = null, // consul://consul.corp.example.com:8600/some/path
    @SerialName("ContainerdCommit")
    val containerdCommit: ContainerdCommit,
    @SerialName("Containers")
    val containers: Int, // 14
    @SerialName("ContainersPaused")
    val containersPaused: Int, // 1
    @SerialName("ContainersRunning")
    val containersRunning: Int, // 3
    @SerialName("ContainersStopped")
    val containersStopped: Int, // 10
    @SerialName("CpuCfsPeriod")
    val cpuCfsPeriod: Boolean, // true
    @SerialName("CpuCfsQuota")
    val cpuCfsQuota: Boolean, // true
    @SerialName("Debug")
    val debug: Boolean, // true
    @SerialName("DefaultAddressPools")
    val defaultAddressPools: List<DefaultAddressPool>? = null,
    @SerialName("DefaultRuntime")
    val defaultRuntime: String, // runc
    @SerialName("DockerRootDir")
    val dockerRootDir: String, // /var/lib/docker
    @SerialName("Driver")
    val driver: String, // overlay2
    @SerialName("DriverStatus")
    val driverStatus: List<List<String>>,
    @SerialName("ExperimentalBuild")
    val experimentalBuild: Boolean, // true
    @SerialName("GenericResources")
    val genericResources: List<GenericResource>?,
    @SerialName("HttpProxy")
    val httpProxy: String, // http://xxxxx:xxxxx@proxy.corp.example.com:8080
    @SerialName("HttpsProxy")
    val httpsProxy: String, // https://xxxxx:xxxxx@proxy.corp.example.com:4443
    @SerialName("ID")
    val iD: String, // 7TRN:IPZB:QYBB:VPBQ:UMPP:KARE:6ZNR:XE6T:7EWV:PKF4:ZOJD:TPYS
    @SerialName("IPv4Forwarding")
    val iPv4Forwarding: Boolean, // true
    @SerialName("Images")
    val images: Int, // 508
    @SerialName("IndexServerAddress")
    val indexServerAddress: String, // https://index.docker.io/v1/
    @SerialName("InitBinary")
    val initBinary: String, // docker-init
    @SerialName("InitCommit")
    val initCommit: InitCommit,
    @SerialName("Isolation")
    val isolation: String, // default
    @SerialName("KernelMemory")
    val kernelMemory: Boolean, // true
    @SerialName("KernelVersion")
    val kernelVersion: String, // 4.9.38-moby
    @SerialName("Labels")
    val labels: List<String>,
    @SerialName("LiveRestoreEnabled")
    val liveRestoreEnabled: Boolean, // false
    @SerialName("LoggingDriver")
    val loggingDriver: String, // string
    @SerialName("MemTotal")
    val memTotal: Long, // 2095882240
    @SerialName("MemoryLimit")
    val memoryLimit: Boolean, // true
    @SerialName("NCPU")
    val nCPU: Int, // 4
    @SerialName("NEventsListener")
    val nEventsListener: Int, // 30
    @SerialName("NFd")
    val nFd: Int, // 64
    @SerialName("NGoroutines")
    val nGoroutines: Int, // 174
    @SerialName("Name")
    val name: String, // node5.corp.example.com
    @SerialName("NoProxy")
    val noProxy: String, // *.local, 169.254/16
    @SerialName("OSType")
    val oSType: String, // linux
    @SerialName("OSVersion")
    val oSVersion: String, // 16.04
    @SerialName("OomKillDisable")
    val oomKillDisable: Boolean, // true
    @SerialName("OperatingSystem")
    val operatingSystem: String, // Alpine Linux v3.5
    @SerialName("PidsLimit")
    val pidsLimit: Boolean, // true
    @SerialName("Plugins")
    val plugins: Plugins,
    @SerialName("ProductLicense")
    val productLicense: String? = null, // Community Engine
    @SerialName("RegistryConfig")
    val registryConfig: RegistryConfig,
    @SerialName("RuncCommit")
    val runcCommit: RuncCommit,
    @SerialName("Runtimes")
    val runtimes: Map<String, Runtime>,
    @SerialName("SecurityOptions")
    val securityOptions: List<String>,
    @SerialName("ServerVersion")
    val serverVersion: String, // 17.06.0-ce
    @SerialName("SwapLimit")
    val swapLimit: Boolean, // true
    @SerialName("Swarm")
    val swarm: Swarm,
    @SerialName("SystemTime")
    @Serializable(with = InstantSerializer::class)
    val systemTime: Instant, // 2017-08-08T20:28:29.06202363Z
    @SerialName("Warnings")
    val warnings: List<String>
) {
    @Serializable
    data class ContainerdCommit(
        @SerialName("Expected")
        val expected: String, // 2d41c047c83e09a6d61d464906feb2a2f3c52aa4
        @SerialName("ID")
        val iD: String // cfb82a876ecc11b5ca0977d1733adbe58599088a
    )

    @Serializable
    data class DefaultAddressPool(
        @SerialName("Base")
        val base: String, // 10.10.0.0/16
        @SerialName("Size")
        val size: String // 24
    )

    @Serializable
    data class GenericResource(
        @SerialName("DiscreteResourceSpec")
        val discreteResourceSpec: DiscreteResourceSpec,
        @SerialName("NamedResourceSpec")
        val namedResourceSpec: NamedResourceSpec
    ) {
        @Serializable
        data class DiscreteResourceSpec(
            @SerialName("Kind")
            val kind: String, // SSD
            @SerialName("Value")
            val value: Int // 3
        )

        @Serializable
        data class NamedResourceSpec(
            @SerialName("Kind")
            val kind: String, // GPU
            @SerialName("Value")
            val value: String // UUID1
        )
    }

    @Serializable
    data class InitCommit(
        @SerialName("Expected")
        val expected: String, // 2d41c047c83e09a6d61d464906feb2a2f3c52aa4
        @SerialName("ID")
        val id: String // cfb82a876ecc11b5ca0977d1733adbe58599088a
    )

    @Serializable
    data class Plugins(
        @SerialName("Authorization")
        val authorization: List<String>?,
        @SerialName("Log")
        val log: List<String>,
        @SerialName("Network")
        val network: List<String>,
        @SerialName("Volume")
        val volume: List<String>
    )

    @Serializable
    data class RegistryConfig(
        @SerialName("AllowNondistributableArtifactsCIDRs")
        val allowNondistributableArtifactsCIDRs: List<String>,
        @SerialName("AllowNondistributableArtifactsHostnames")
        val allowNondistributableArtifactsHostnames: List<String>,
        @SerialName("IndexConfigs")
        val indexConfigs: Map<String, IndexConfig>,
        @SerialName("InsecureRegistryCIDRs")
        val insecureRegistryCIDRs: List<String>,
        @SerialName("Mirrors")
        val mirrors: List<String>
    ) {
        @Serializable
        data class IndexConfig(
            @SerialName("Mirrors")
            val mirrors: List<String>,
            @SerialName("Name")
            val name: String, // docker.io
            @SerialName("Official")
            val official: Boolean, // true
            @SerialName("Secure")
            val secure: Boolean // true
        )
    }

    @Serializable
    data class RuncCommit(
        @SerialName("Expected")
        val expected: String, // 2d41c047c83e09a6d61d464906feb2a2f3c52aa4
        @SerialName("ID")
        val iD: String // cfb82a876ecc11b5ca0977d1733adbe58599088a
    )

    @Serializable
    data class Runtime(
        @SerialName("path")
        val path: String, // /usr/local/bin/my-oci-runtime
        @SerialName("runtimeArgs")
        val runtimeArgs: List<String>? = null
    )

    @Serializable
    data class Swarm(
        @SerialName("Cluster")
        val cluster: Cluster? = null,
        @SerialName("ControlAvailable")
        val controlAvailable: Boolean, // true
        @SerialName("Error")
        val error: String,
        @SerialName("LocalNodeState")
        val localNodeState: String, // active
        @SerialName("Managers")
        val managers: Int? = null, // 3
        @SerialName("NodeAddr")
        val nodeAddr: String, // 10.0.0.46
        @SerialName("NodeID")
        val nodeID: String, // k67qz4598weg5unwwffg6z1m1
        @SerialName("Nodes")
        val nodes: Int? = null, // 4
        @SerialName("RemoteManagers")
        val remoteManagers: List<RemoteManager>?
    ) {
        @Serializable
        data class Cluster(
            @SerialName("CreatedAt")
            val createdAt: String, // 2016-08-18T10:44:24.496525531Z
            @SerialName("DataPathPort")
            val dataPathPort: Int, // 4789
            @SerialName("DefaultAddrPool")
            val defaultAddrPool: List<List<String>>,
            @SerialName("ID")
            val iD: String, // abajmipo7b4xz5ip2nrla6b11
            @SerialName("RootRotationInProgress")
            val rootRotationInProgress: Boolean, // false
            @SerialName("Spec")
            val spec: Spec,
            @SerialName("SubnetSize")
            val subnetSize: Int, // 24
            @SerialName("TLSInfo")
            val tLSInfo: TLSInfo,
            @SerialName("UpdatedAt")
            @Serializable(with = InstantSerializer::class)
            val updatedAt: Instant, // 2017-08-09T07:09:37.632105588Z
            @SerialName("Version")
            val version: Version
        ) {
            @Serializable
            data class Spec(
                @SerialName("CAConfig")
                val caConfig: CAConfig,
                @SerialName("Dispatcher")
                val dispatcher: Dispatcher,
                @SerialName("EncryptionConfig")
                val encryptionConfig: EncryptionConfig,
                @SerialName("Labels")
                val labels: Map<String, String>,
                @SerialName("Name")
                val name: String, // default
                @SerialName("Orchestration")
                val orchestration: Orchestration,
                @SerialName("Raft")
                val raft: Raft,
                @SerialName("TaskDefaults")
                val taskDefaults: TaskDefaults
            ) {
                @Serializable
                data class CAConfig(
                    @SerialName("ExternalCAs")
                    val externalCAs: List<ExternalCA>,
                    @SerialName("ForceRotate")
                    val forceRotate: Int, // 0
                    @SerialName("NodeCertExpiry")
                    val nodeCertExpiry: Long, // 7776000000000000
                    @SerialName("SigningCACert")
                    val signingCACert: String, // string
                    @SerialName("SigningCAKey")
                    val signingCAKey: String // string
                ) {
                    @Serializable
                    data class ExternalCA(
                        @SerialName("CACert")
                        val caCert: String, // string
                        @SerialName("Options")
                        val options: Map<String, String>,
                        @SerialName("Protocol")
                        val protocol: String, // cfssl
                        @SerialName("URL")
                        val url: String // string
                    )
                }

                @Serializable
                data class Dispatcher(
                    @SerialName("HeartbeatPeriod")
                    val heartbeatPeriod: Long // 5000000000
                )

                @Serializable
                data class EncryptionConfig(
                    @SerialName("AutoLockManagers")
                    val autoLockManagers: Boolean // false
                )

                @Serializable
                data class Orchestration(
                    @SerialName("TaskHistoryRetentionLimit")
                    val taskHistoryRetentionLimit: Int // 10
                )

                @Serializable
                data class Raft(
                    @SerialName("ElectionTick")
                    val electionTick: Int, // 3
                    @SerialName("HeartbeatTick")
                    val heartbeatTick: Int, // 1
                    @SerialName("KeepOldSnapshots")
                    val keepOldSnapshots: Int, // 0
                    @SerialName("LogEntriesForSlowFollowers")
                    val logEntriesForSlowFollowers: Int, // 500
                    @SerialName("SnapshotInterval")
                    val snapshotInterval: Int // 10000
                )

                @Serializable
                data class TaskDefaults(
                    @SerialName("LogDriver")
                    val logDriver: LogDriver
                ) {
                    @Serializable
                    data class LogDriver(
                        @SerialName("Name")
                        val name: String, // json-file
                        @SerialName("Options")
                        val options: Map<String, String>
                    )
                }
            }

            @Serializable
            data class TLSInfo(
                @SerialName("CertIssuerPublicKey")
                val certIssuerPublicKey: String, // MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEmT9XIw9h1qoNclv9VeHmf/Vi6/uI2vFXdBveXTpcPjqx6i9wNazchk1XWV/dKTKvSh9xyGKmiIeRcE4OiMnJ1A==
                @SerialName("CertIssuerSubject")
                val certIssuerSubject: String, // MBMxETAPBgNVBAMTCHN3YXJtLWNh
                @SerialName("TrustRoot")
                val trustRoot: String // -----BEGIN CERTIFICATE-----MIIBajCCARCgAwIBAgIUbYqrLSOSQHoxD8CwG6Bi2PJi9c8wCgYIKoZIzj0EAwIwEzERMA8GA1UEAxMIc3dhcm0tY2EwHhcNMTcwNDI0MjE0MzAwWhcNMzcwNDE5MjE0MzAwWjATMREwDwYDVQQDEwhzd2FybS1jYTBZMBMGByqGSM49AgEGCCqGSM49AwEHA0IABJk/VyMPYdaqDXJb/VXh5n/1Yuv7iNrxV3Qb3l06XD46seovcDWs3IZNV1lf3Skyr0ofcchipoiHkXBODojJydSjQjBAMA4GA1UdDwEB/wQEAwIBBjAPBgNVHRMBAf8EBTADAQH/MB0GA1UdDgQWBBRUXxuRcnFjDfR/RIAUQab8ZV/n4jAKBggqhkjOPQQDAgNIADBFAiAy+JTe6Uc3KyLCMiqGl2GyWGQqQDEcO3/YG36x7om65AIhAJvzpxv6zFeVEkAEEkqIYi0omA9+CjanB/6Bz4n1uw8H-----END CERTIFICATE-----
            )

            @Serializable
            data class Version(
                @SerialName("Index")
                val index: Int // 373531
            )
        }

        @Serializable
        data class RemoteManager(
            @SerialName("Addr")
            val addr: String, // 10.0.0.158:2377
            @SerialName("NodeID")
            val nodeID: String // 71izy0goik036k48jg985xnds
        )
    }
}