package com.kairlec.koj.core

import com.kairlec.koj.common.LanguageIdContent

interface Language : LanguageIdContent {
    val name: String
    val extension: String
    val version: String
}

interface LanguageSupport {
    fun isSupported(language: Language): Boolean
}