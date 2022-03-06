package com.kairlec.koj.sandbox.docker

data class DockerSandboxCompileConfig(
    val sourceFileName: String,
    val sourceContent:String,
    val namespace:String,
    val image:String,
    val kojEnv: KOJEnv
)