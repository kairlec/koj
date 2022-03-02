package com.kairlec.koj.core

interface KojCompiler : LanguageSupport {
    /**
     * Compile the source code to bytecode.
     */
    context (KojContext) suspend fun compile(compileConfig: CompileConfig): CompileResult

    val name: String
}

sealed interface CompileResult

interface CompileSuccess : CompileResult

interface CompileFailure : CompileResult {
    val message: String
    val cause: Throwable?
}

interface CompileConfig {
    val source: CompileSource
}

interface CompileSource {
    val source: String
}
