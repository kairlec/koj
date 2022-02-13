@file:Suppress("ClassName")

package com.kairlec.koj.language.kotlin

import com.google.auto.service.AutoService
import com.kairlec.koj.core.Language
import com.kairlec.koj.language.java.Java11
import com.kairlec.koj.language.java.Java17
import com.kairlec.koj.language.java.Java8

@AutoService(Language::class)
object `Kotlin1610-Java8` : Kotlin1610(Java8)

@AutoService(Language::class)
object `Kotlin1610-Java11` : Kotlin1610(Java11)

@AutoService(Language::class)
object `Kotlin1610-Java17` : Kotlin1610(Java17)