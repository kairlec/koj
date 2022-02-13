package com.kairlec.koj.language.java

import com.google.auto.service.AutoService
import com.kairlec.koj.core.Language

@AutoService(Language::class)
object Java8 : Java() {
    override val version: String
        get() = "1.8"
}

@AutoService(Language::class)
object Java11: Java() {
    override val version: String
        get() = "11"
}

@AutoService(Language::class)
object Java17 : Java() {
    override val version: String
        get() = "17"
}