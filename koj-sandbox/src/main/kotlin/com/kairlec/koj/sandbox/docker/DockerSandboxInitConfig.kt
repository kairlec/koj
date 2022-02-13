package com.kairlec.koj.sandbox.docker

import com.kairlec.koj.sandbox.SandboxInitConfig

data class DockerSandboxInitConfig(
    val prepareImages: List<String>
) : SandboxInitConfig