package com.kairlec.koj.language

import com.kairlec.koj.core.Language
import java.util.*

abstract class Jvm : Language {
    open val systemPropertyDefine: Properties = Properties().apply {
        setProperty("ONLINE_JUDGE", "ONLINE_JUDGE")
    }
}