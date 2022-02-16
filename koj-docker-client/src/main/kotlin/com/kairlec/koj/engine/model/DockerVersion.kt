package com.kairlec.koj.engine.model


import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Instant

data class DockerVersion(
    @JsonProperty("ApiVersion")
    val apiVersion: String, // 1.40
    @JsonProperty("Arch")
    val arch: String, // amd64
    @JsonProperty("BuildTime")
    val buildTime: Instant, // 2020-06-22T15:49:27.000000000+00:00
    @JsonProperty("Components")
    val components: List<Component>,
    @JsonProperty("Experimental")
    val experimental: Boolean = false, // true
    @JsonProperty("GitCommit")
    val gitCommit: String, // 48a66213fe
    @JsonProperty("GoVersion")
    val goVersion: String, // go1.13.14
    @JsonProperty("KernelVersion")
    val kernelVersion: String, // 4.19.76-linuxkit
    @JsonProperty("MinAPIVersion")
    val minAPIVersion: String, // 1.12
    @JsonProperty("Os")
    val os: String, // linux
    @JsonProperty("Platform")
    val platform: Platform,
    @JsonProperty("Version")
    val version: String // 19.03.12
) {
    data class Component(
        @JsonProperty("Details")
        val details: Map<String, String>,
        @JsonProperty("Name")
        val name: String, // Engine
        @JsonProperty("Version")
        val version: String // 19.03.12
    )

    data class Platform(
        @JsonProperty("Name")
        val name: String // string
    )
}