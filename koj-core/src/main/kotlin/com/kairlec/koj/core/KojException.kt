package com.kairlec.koj.core

import com.kairlec.koj.sandbox.SandboxOutput

abstract class KojException(override val message: String, override val cause: Throwable?) :
    RuntimeException(message, cause)


abstract class CompilerException(override val message: String, override val cause: Throwable?) :
    KojException(message, cause)

class UnsupportedLanguageException(
    val language: Language,
    override val message: String,
    override val cause: Throwable? = null
) : CompilerException(message, cause)

class SandboxCompileException(
    val output: SandboxOutput,
    override val message: String = output.stdout?.value ?: "",
    override val cause: Throwable? = null
) : CompilerException(message, cause)

abstract class ExecuteException(override val message: String, override val cause: Throwable?) :
    KojException(message, cause)

class SandboxExecuteException(
    val output: SandboxOutput,
    override val message: String = output.stdout?.value ?: "",
    override val cause: Throwable? = null
) : ExecuteException(message, cause)