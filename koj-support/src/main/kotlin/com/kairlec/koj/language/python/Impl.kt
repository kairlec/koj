package com.kairlec.koj.language.python

import com.google.auto.service.AutoService
import com.kairlec.koj.core.Language

@AutoService(Language::class)
object Python27 : Python() {
    override val version: String
        get() = "2.7"
}

@AutoService(Language::class)
object Python36 : Python() {
    override val version: String
        get() = "3.6"
}

@AutoService(Language::class)
object Python38 : Python() {
    override val version: String
        get() = "3.8"
}

@AutoService(Language::class)
object Python310 : Python() {
    override val version: String
        get() = "3.10"
}