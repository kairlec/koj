package com.kairlec.koj.language.c

import com.google.auto.service.AutoService
import com.kairlec.koj.core.Language

@AutoService(Language::class)
object C11 : C() {
    override val version: String get() = "11"
}

@AutoService(Language::class)
object C99 : C() {
    override val version: String get() = "99"
}