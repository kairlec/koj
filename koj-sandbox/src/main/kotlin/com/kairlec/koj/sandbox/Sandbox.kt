package com.kairlec.koj.sandbox

import java.io.InputStream

interface Sandbox<I : SandboxInitConfig, R : SandboxRunConfig, C : SandboxCompileConfig> {
    fun init(initConfig: I)
    fun run(runConfig: R): SandboxOutput
    fun compile(compileConfig: C): String
}