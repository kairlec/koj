package com.kairlec.koj.core

abstract class CompilerException(override val message: String, override val cause: Throwable?) :
    RuntimeException(message, cause)


class UnsupportedLanguageException(
    val language: Language,
    override val message: String,
    override val cause: Throwable? = null
) : CompilerException(message, cause)