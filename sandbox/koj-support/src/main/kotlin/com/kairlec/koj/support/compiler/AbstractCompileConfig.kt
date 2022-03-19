package com.kairlec.koj.support.compiler

import com.kairlec.koj.core.CompileConfig

abstract class AbstractCompileConfig : CompileConfig {
    abstract val compileImage: String
    abstract val compileImageVersion: String
}