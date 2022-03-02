package com.kairlec.koj.sandbox

import com.kairlec.koj.common.TempDirectory

interface Sandbox<I : SandboxInitConfig, R : SandboxRunConfig, C : SandboxCompileConfig> {
    suspend fun init(initConfig: I)
    suspend fun run(
        tempDirectory: TempDirectory,
        runConfig: R
    ): SandboxOutput

    suspend fun compile(
        tempDirectory: TempDirectory,
        compileConfig: C
    ): SandboxOutput
}