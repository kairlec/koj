package com.kairlec.koj.language.python

import com.kairlec.koj.core.Language

abstract class Python : Language {
    override val name: String
        get() = "Python"
    override val extension: String
        get() = ".py"
    override val id: String
        get() = "Python $version"
}