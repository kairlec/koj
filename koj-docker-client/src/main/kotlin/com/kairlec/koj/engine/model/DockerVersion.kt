package com.kairlec.koj.engine.model


import com.kairlec.koj.engine.serialzer.InstantSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class DockerVersion(
    @SerialName("ApiVersion")
    val apiVersion: String, // 1.40
    @SerialName("Arch")
    val arch: String, // amd64
    @SerialName("BuildTime")
    @Serializable(with = InstantSerializer::class)
    val buildTime: Instant, // 2020-06-22T15:49:27.000000000+00:00
    @SerialName("Components")
    val components: List<Component>,
    @SerialName("Experimental")
    val experimental: Boolean = false, // true
    @SerialName("GitCommit")
    val gitCommit: String, // 48a66213fe
    @SerialName("GoVersion")
    val goVersion: String, // go1.13.14
    @SerialName("KernelVersion")
    val kernelVersion: String, // 4.19.76-linuxkit
    @SerialName("MinAPIVersion")
    val minAPIVersion: String, // 1.12
    @SerialName("Os")
    val os: String, // linux
    @SerialName("Platform")
    val platform: Platform,
    @SerialName("Version")
    val version: String // 19.03.12
) {
    @Serializable
    data class Component(
        @SerialName("Details")
        val details: Details,
        @SerialName("Name")
        val name: String, // Engine
        @SerialName("Version")
        val version: String // 19.03.12
    ) {
        @Serializable
        class Details
    }

    @Serializable
    data class Platform(
        @SerialName("Name")
        val name: String // string
    )
}