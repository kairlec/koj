package com.kairlec.koj.sandbox.docker

import com.kairlec.koj.sandbox.SandboxInitException

class DockerSandboxInitImageFailedException(
    val image: String,
    override val cause: Throwable?
) : SandboxInitException("Docker image:$image init failed", cause)