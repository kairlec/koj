package com.kairlec.koj.sandbox.docker

import com.kairlec.koj.sandbox.SandboxRunConfig

class DockerSandboxRunConfig(
    val image: String,
    val workspace: String,
    val executable: List<String>,
    val namespace: String,
    val input:String
) : SandboxRunConfig