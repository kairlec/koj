package com.kairlec.koj.core

interface Language {
    val id: String
    val name: String
    val extension: String
    val version: String
}

interface LanguageSupport {
    fun isSupported(language: Language): Boolean
}