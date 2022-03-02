package com.kairlec.koj.core

import com.kairlec.koj.common.TempDirectory

interface KojContext {
    val id: Long
    val namespace: String
    val tempDirectory: TempDirectory
    val useLanguage: Language
    val useCompiler: KojCompiler
    val useExecutor: KojExecutor
    val problemId: Int
}