package com.kairlec.koj.core

abstract class AbstractKojCompiler : KojCompiler {
    final override fun compile(compileConfig: CompileConfig): CompileResult {
        if (!isSupported(compileConfig.language)) {
            throw UnsupportedLanguageException(
                compileConfig.language,
                "not supported language:${compileConfig.language} using compiler:${name}"
            )
        }
        return internalCompile(compileConfig)
    }

    protected abstract fun internalCompile(compileConfig: CompileConfig): CompileResult

}