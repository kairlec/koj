package com.kairlec.koj.language.python


object Python27 : Python() {
    override val version: String
        get() = "2.7"
}

object Python36 : Python() {
    override val version: String
        get() = "3.6"
}

object Python38 : Python() {
    override val version: String
        get() = "3.8"
}

object Python310 : Python() {
    override val version: String
        get() = "3.10"
}