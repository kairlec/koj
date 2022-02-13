package com.kairlec.koj.core

sealed interface CompileResult

interface CompileSuccess : CompileResult {
    val byteCode: ByteArray
    val message: String
}

interface CompileError : CompileResult {
    val message: String
    val cause: Throwable?
}