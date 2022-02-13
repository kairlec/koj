package com.kairlec.koj.sandbox

abstract class SandboxException(override val message: String, override val cause: Throwable? = null) :
    RuntimeException(message, cause)

abstract class SandboxInitException(override val message: String, override val cause: Throwable? = null) :
    SandboxException(message, cause)
