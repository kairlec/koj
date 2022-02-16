package com.kairlec.koj.engine.model


import com.google.gson.annotations.SerializedName
import com.kairlec.koj.engine.serialzer.InstantTypeAdapter
import java.time.Instant

data class DockerInfo(
    @SerializedName("Architecture")
    val architecture: String, // x86_64
    @SerializedName("BridgeNfIp6tables")
    val bridgeNfIp6tables: Boolean, // true
    @SerializedName("BridgeNfIptables")
    val bridgeNfIptables: Boolean, // true
    @SerializedName("CPUSet")
    val cPUSet: Boolean, // true
    @SerializedName("CPUShares")
    val cPUShares: Boolean, // true
    @SerializedName("CgroupDriver")
    val cgroupDriver: String, // cgroupfs
    @SerializedName("CgroupVersion")
    val cgroupVersion: String, // 1
    @SerializedName("ClusterAdvertise")
    val clusterAdvertise: String? = null, // node5.corp.example.com:8000
    @SerializedName("ClusterStore")
    val clusterStore: String? = null, // consul://consul.corp.example.com:8600/some/path
    @SerializedName("ContainerdCommit")
    val containerdCommit: ContainerdCommit,
    @SerializedName("Containers")
    val containers: Int, // 14
    @SerializedName("ContainersPaused")
    val containersPaused: Int, // 1
    @SerializedName("ContainersRunning")
    val containersRunning: Int, // 3
    @SerializedName("ContainersStopped")
    val containersStopped: Int, // 10
    @SerializedName("CpuCfsPeriod")
    val cpuCfsPeriod: Boolean, // true
    @SerializedName("CpuCfsQuota")
    val cpuCfsQuota: Boolean, // true
    @SerializedName("Debug")
    val debug: Boolean, // true
    @SerializedName("DefaultAddressPools")
    val defaultAddressPools: List<DefaultAddressPool>? = null,
    @SerializedName("DefaultRuntime")
    val defaultRuntime: String, // runc
    @SerializedName("DockerRootDir")
    val dockerRootDir: String, // /var/lib/docker
    @SerializedName("Driver")
    val driver: String, // overlay2
    @SerializedName("DriverStatus")
    val driverStatus: List<List<String>>,
    @SerializedName("ExperimentalBuild")
    val experimentalBuild: Boolean, // true
    @SerializedName("GenericResources")
    val genericResources: List<GenericResource>?,
    @SerializedName("HttpProxy")
    val httpProxy: String, // http://xxxxx:xxxxx@proxy.corp.example.com:8080
    @SerializedName("HttpsProxy")
    val httpsProxy: String, // https://xxxxx:xxxxx@proxy.corp.example.com:4443
    @SerializedName("ID")
    val iD: String, // 7TRN:IPZB:QYBB:VPBQ:UMPP:KARE:6ZNR:XE6T:7EWV:PKF4:ZOJD:TPYS
    @SerializedName("IPv4Forwarding")
    val iPv4Forwarding: Boolean, // true
    @SerializedName("Images")
    val images: Int, // 508
    @SerializedName("IndexServerAddress")
    val indexServerAddress: String, // https://index.docker.io/v1/
    @SerializedName("InitBinary")
    val initBinary: String, // docker-init
    @SerializedName("InitCommit")
    val initCommit: InitCommit,
    @SerializedName("Isolation")
    val isolation: String, // default
    @SerializedName("KernelMemory")
    val kernelMemory: Boolean, // true
    @SerializedName("KernelVersion")
    val kernelVersion: String, // 4.9.38-moby
    @SerializedName("Labels")
    val labels: List<String>,
    @SerializedName("LiveRestoreEnabled")
    val liveRestoreEnabled: Boolean, // false
    @SerializedName("LoggingDriver")
    val loggingDriver: String, // string
    @SerializedName("MemTotal")
    val memTotal: Long, // 2095882240
    @SerializedName("MemoryLimit")
    val memoryLimit: Boolean, // true
    @SerializedName("NCPU")
    val nCPU: Int, // 4
    @SerializedName("NEventsListener")
    val nEventsListener: Int, // 30
    @SerializedName("NFd")
    val nFd: Int, // 64
    @SerializedName("NGoroutines")
    val nGoroutines: Int, // 174
    @SerializedName("Name")
    val name: String, // node5.corp.example.com
    @SerializedName("NoProxy")
    val noProxy: String, // *.local, 169.254/16
    @SerializedName("OSType")
    val oSType: String, // linux
    @SerializedName("OSVersion")
    val oSVersion: String, // 16.04
    @SerializedName("OomKillDisable")
    val oomKillDisable: Boolean, // true
    @SerializedName("OperatingSystem")
    val operatingSystem: String, // Alpine Linux v3.5
    @SerializedName("PidsLimit")
    val pidsLimit: Boolean, // true
    @SerializedName("Plugins")
    val plugins: Plugins,
    @SerializedName("ProductLicense")
    val productLicense: String? = null, // Community Engine
    @SerializedName("RegistryConfig")
    val registryConfig: RegistryConfig,
    @SerializedName("RuncCommit")
    val runcCommit: RuncCommit,
    @SerializedName("Runtimes")
    val runtimes: Map<String, Runtime>,
    @SerializedName("SecurityOptions")
    val securityOptions: List<String>,
    @SerializedName("ServerVersion")
    val serverVersion: String, // 17.06.0-ce
    @SerializedName("SwapLimit")
    val swapLimit: Boolean, // true
    @SerializedName("Swarm")
    val swarm: Swarm,
    @SerializedName("SystemTime")
    val systemTime: Instant, // 2017-08-08T20:28:29.06202363Z
    @SerializedName("Warnings")
    val warnings: List<String>
) {
    data class ContainerdCommit(
        @SerializedName("Expected")
        val expected: String, // 2d41c047c83e09a6d61d464906feb2a2f3c52aa4
        @SerializedName("ID")
        val iD: String // cfb82a876ecc11b5ca0977d1733adbe58599088a
    )

    data class DefaultAddressPool(
        @SerializedName("Base")
        val base: String, // 10.10.0.0/16
        @SerializedName("Size")
        val size: String // 24
    )

    data class GenericResource(
        @SerializedName("DiscreteResourceSpec")
        val discreteResourceSpec: DiscreteResourceSpec,
        @SerializedName("NamedResourceSpec")
        val namedResourceSpec: NamedResourceSpec
    ) {
        data class DiscreteResourceSpec(
            @SerializedName("Kind")
            val kind: String, // SSD
            @SerializedName("Value")
            val value: Int // 3
        )

        data class NamedResourceSpec(
            @SerializedName("Kind")
            val kind: String, // GPU
            @SerializedName("Value")
            val value: String // UUID1
        )
    }

    data class InitCommit(
        @SerializedName("Expected")
        val expected: String, // 2d41c047c83e09a6d61d464906feb2a2f3c52aa4
        @SerializedName("ID")
        val id: String // cfb82a876ecc11b5ca0977d1733adbe58599088a
    )

    data class Plugins(
        @SerializedName("Authorization")
        val authorization: List<String>?,
        @SerializedName("Log")
        val log: List<String>,
        @SerializedName("Network")
        val network: List<String>,
        @SerializedName("Volume")
        val volume: List<String>
    )

    data class RegistryConfig(
        @SerializedName("AllowNondistributableArtifactsCIDRs")
        val allowNondistributableArtifactsCIDRs: List<String>,
        @SerializedName("AllowNondistributableArtifactsHostnames")
        val allowNondistributableArtifactsHostnames: List<String>,
        @SerializedName("IndexConfigs")
        val indexConfigs: Map<String, IndexConfig>,
        @SerializedName("InsecureRegistryCIDRs")
        val insecureRegistryCIDRs: List<String>,
        @SerializedName("Mirrors")
        val mirrors: List<String>
    ) {
        data class IndexConfig(
            @SerializedName("Mirrors")
            val mirrors: List<String>,
            @SerializedName("Name")
            val name: String, // docker.io
            @SerializedName("Official")
            val official: Boolean, // true
            @SerializedName("Secure")
            val secure: Boolean // true
        )
    }

    data class RuncCommit(
        @SerializedName("Expected")
        val expected: String, // 2d41c047c83e09a6d61d464906feb2a2f3c52aa4
        @SerializedName("ID")
        val iD: String // cfb82a876ecc11b5ca0977d1733adbe58599088a
    )

    data class Runtime(
        @SerializedName("path")
        val path: String, // /usr/local/bin/my-oci-runtime
        @SerializedName("runtimeArgs")
        val runtimeArgs: List<String>? = null
    )

    data class Swarm(
        @SerializedName("Cluster")
        val cluster: Cluster? = null,
        @SerializedName("ControlAvailable")
        val controlAvailable: Boolean, // true
        @SerializedName("Error")
        val error: String,
        @SerializedName("LocalNodeState")
        val localNodeState: String, // active
        @SerializedName("Managers")
        val managers: Int? = null, // 3
        @SerializedName("NodeAddr")
        val nodeAddr: String, // 10.0.0.46
        @SerializedName("NodeID")
        val nodeID: String, // k67qz4598weg5unwwffg6z1m1
        @SerializedName("Nodes")
        val nodes: Int? = null, // 4
        @SerializedName("RemoteManagers")
        val remoteManagers: List<RemoteManager>?
    ) {
        data class Cluster(
            @SerializedName("CreatedAt")
            val createdAt: String, // 2016-08-18T10:44:24.496525531Z
            @SerializedName("DataPathPort")
            val dataPathPort: Int, // 4789
            @SerializedName("DefaultAddrPool")
            val defaultAddrPool: List<List<String>>,
            @SerializedName("ID")
            val iD: String, // abajmipo7b4xz5ip2nrla6b11
            @SerializedName("RootRotationInProgress")
            val rootRotationInProgress: Boolean, // false
            @SerializedName("Spec")
            val spec: Spec,
            @SerializedName("SubnetSize")
            val subnetSize: Int, // 24
            @SerializedName("TLSInfo")
            val tLSInfo: TLSInfo,
            @SerializedName("UpdatedAt")
            val updatedAt: Instant, // 2017-08-09T07:09:37.632105588Z
            @SerializedName("Version")
            val version: Version
        ) {
            data class Spec(
                @SerializedName("CAConfig")
                val caConfig: CAConfig,
                @SerializedName("Dispatcher")
                val dispatcher: Dispatcher,
                @SerializedName("EncryptionConfig")
                val encryptionConfig: EncryptionConfig,
                @SerializedName("Labels")
                val labels: Map<String, String>,
                @SerializedName("Name")
                val name: String, // default
                @SerializedName("Orchestration")
                val orchestration: Orchestration,
                @SerializedName("Raft")
                val raft: Raft,
                @SerializedName("TaskDefaults")
                val taskDefaults: TaskDefaults
            ) {
                data class CAConfig(
                    @SerializedName("ExternalCAs")
                    val externalCAs: List<ExternalCA>,
                    @SerializedName("ForceRotate")
                    val forceRotate: Int, // 0
                    @SerializedName("NodeCertExpiry")
                    val nodeCertExpiry: Long, // 7776000000000000
                    @SerializedName("SigningCACert")
                    val signingCACert: String, // string
                    @SerializedName("SigningCAKey")
                    val signingCAKey: String // string
                ) {
                    data class ExternalCA(
                        @SerializedName("CACert")
                        val caCert: String, // string
                        @SerializedName("Options")
                        val options: Map<String, String>,
                        @SerializedName("Protocol")
                        val protocol: String, // cfssl
                        @SerializedName("URL")
                        val url: String // string
                    )
                }

                data class Dispatcher(
                    @SerializedName("HeartbeatPeriod")
                    val heartbeatPeriod: Long // 5000000000
                )

                data class EncryptionConfig(
                    @SerializedName("AutoLockManagers")
                    val autoLockManagers: Boolean // false
                )

                data class Orchestration(
                    @SerializedName("TaskHistoryRetentionLimit")
                    val taskHistoryRetentionLimit: Int // 10
                )

                data class Raft(
                    @SerializedName("ElectionTick")
                    val electionTick: Int, // 3
                    @SerializedName("HeartbeatTick")
                    val heartbeatTick: Int, // 1
                    @SerializedName("KeepOldSnapshots")
                    val keepOldSnapshots: Int, // 0
                    @SerializedName("LogEntriesForSlowFollowers")
                    val logEntriesForSlowFollowers: Int, // 500
                    @SerializedName("SnapshotInterval")
                    val snapshotInterval: Int // 10000
                )

                data class TaskDefaults(
                    @SerializedName("LogDriver")
                    val logDriver: LogDriver
                ) {
                    data class LogDriver(
                        @SerializedName("Name")
                        val name: String, // json-file
                        @SerializedName("Options")
                        val options: Map<String, String>
                    )
                }
            }

            data class TLSInfo(
                @SerializedName("CertIssuerPublicKey")
                val certIssuerPublicKey: String, // MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEmT9XIw9h1qoNclv9VeHmf/Vi6/uI2vFXdBveXTpcPjqx6i9wNazchk1XWV/dKTKvSh9xyGKmiIeRcE4OiMnJ1A==
                @SerializedName("CertIssuerSubject")
                val certIssuerSubject: String, // MBMxETAPBgNVBAMTCHN3YXJtLWNh
                @SerializedName("TrustRoot")
                val trustRoot: String // -----BEGIN CERTIFICATE-----MIIBajCCARCgAwIBAgIUbYqrLSOSQHoxD8CwG6Bi2PJi9c8wCgYIKoZIzj0EAwIwEzERMA8GA1UEAxMIc3dhcm0tY2EwHhcNMTcwNDI0MjE0MzAwWhcNMzcwNDE5MjE0MzAwWjATMREwDwYDVQQDEwhzd2FybS1jYTBZMBMGByqGSM49AgEGCCqGSM49AwEHA0IABJk/VyMPYdaqDXJb/VXh5n/1Yuv7iNrxV3Qb3l06XD46seovcDWs3IZNV1lf3Skyr0ofcchipoiHkXBODojJydSjQjBAMA4GA1UdDwEB/wQEAwIBBjAPBgNVHRMBAf8EBTADAQH/MB0GA1UdDgQWBBRUXxuRcnFjDfR/RIAUQab8ZV/n4jAKBggqhkjOPQQDAgNIADBFAiAy+JTe6Uc3KyLCMiqGl2GyWGQqQDEcO3/YG36x7om65AIhAJvzpxv6zFeVEkAEEkqIYi0omA9+CjanB/6Bz4n1uw8H-----END CERTIFICATE-----
            )

            data class Version(
                @SerializedName("Index")
                val index: Int // 373531
            )
        }

        data class RemoteManager(
            @SerializedName("Addr")
            val addr: String, // 10.0.0.158:2377
            @SerializedName("NodeID")
            val nodeID: String // 71izy0goik036k48jg985xnds
        )
    }
}