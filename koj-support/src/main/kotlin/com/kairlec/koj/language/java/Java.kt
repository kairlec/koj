package com.kairlec.koj.language.java

import com.kairlec.koj.core.Language
import java.util.*

abstract class Java : Language {
    override val name: String
        get() = "Java"
    override val extension: String
        get() = ".java"

    open val systemPropertyDefine: Properties = Properties().apply {
        setProperty("ONLINE_JUDGE", "ONLINE_JUDGE")
    }

    override fun toString(): String {
        return "Java ($version)"
    }
}