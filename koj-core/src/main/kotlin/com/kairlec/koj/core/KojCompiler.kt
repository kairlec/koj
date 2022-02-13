package com.kairlec.koj.core

interface KojCompiler : LanguageSupport {
    /**
     * Compile the source code to bytecode.
     */
    fun compile(compileConfig: CompileConfig): CompileResult

    val name: String
}
