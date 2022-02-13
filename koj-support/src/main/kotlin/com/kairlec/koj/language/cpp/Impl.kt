package com.kairlec.koj.language.cpp

import com.google.auto.service.AutoService
import com.kairlec.koj.core.Language

@AutoService(Language::class)
object CPP98 : CPP() {
    override val version: String get() = "98"
}

@AutoService(Language::class)
object CPP03 : CPP() {
    override val version: String get() = "03"
}

@AutoService(Language::class)
object CPP11 : CPP() {
    override val version: String get() = "11"
}

@AutoService(Language::class)
object CPP14 : CPP() {
    override val version: String get() = "14"
}

@AutoService(Language::class)
object CPP17 : CPP() {
    override val version: String get() = "17"
}

@AutoService(Language::class)
object CPP20 : CPP() {
    override val version: String get() = "20"
}