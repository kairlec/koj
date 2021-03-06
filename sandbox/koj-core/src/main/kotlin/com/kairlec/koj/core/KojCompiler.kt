package com.kairlec.koj.core

interface KojCompiler : LanguageSupport {
    /**
     * Compile the source code to bytecode.
     */
    suspend fun compile(context: KojContext, compileConfig: CompileConfig): CompileResult

    val name: String
}

sealed interface CompileResult {
    val stdout: String
    val stderr: String
}

interface CompileSuccess : CompileResult

interface CompileFailure : CompileResult {
    val message: String
    val cause: Throwable?
}

interface CompileConfig {
    val source: CompileSource
    val debug: Boolean
}

interface CompileSource {
    val source: String
}

fun CompileSource(source: String): CompileSource {
    return CompileSourceImpl(source)
}

internal data class CompileSourceImpl(override val source: String) : CompileSource
