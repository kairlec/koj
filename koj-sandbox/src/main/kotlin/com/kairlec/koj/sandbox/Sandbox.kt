package com.kairlec.koj.sandbox

import java.io.InputStream
import java.io.OutputStream

interface Sandbox<I : SandboxInitConfig, R : SandboxRunConfig, C : SandboxCompileConfig> {
    fun init(initConfig: I)
    fun run(runConfig: R): InputStream
    fun compile(compileConfig: C): InputStream
}