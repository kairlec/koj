package com.kairlec.koj.engine.model


import com.google.gson.annotations.SerializedName
import java.time.Instant

data class DockerVersion(
    @SerializedName("ApiVersion")
    val apiVersion: String, // 1.40
    @SerializedName("Arch")
    val arch: String, // amd64
    @SerializedName("BuildTime")
    val buildTime: Instant, // 2020-06-22T15:49:27.000000000+00:00
    @SerializedName("Components")
    val components: List<Component>,
    @SerializedName("Experimental")
    val experimental: Boolean = false, // true
    @SerializedName("GitCommit")
    val gitCommit: String, // 48a66213fe
    @SerializedName("GoVersion")
    val goVersion: String, // go1.13.14
    @SerializedName("KernelVersion")
    val kernelVersion: String, // 4.19.76-linuxkit
    @SerializedName("MinAPIVersion")
    val minAPIVersion: String, // 1.12
    @SerializedName("Os")
    val os: String, // linux
    @SerializedName("Platform")
    val platform: Platform,
    @SerializedName("Version")
    val version: String // 19.03.12
) {
    data class Component(
        @SerializedName("Details")
        val details: Map<String, String>,
        @SerializedName("Name")
        val name: String, // Engine
        @SerializedName("Version")
        val version: String // 19.03.12
    )

    data class Platform(
        @SerializedName("Name")
        val name: String // string
    )
}