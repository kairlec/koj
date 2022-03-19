package com.kairlec.koj.support

val Int.ms: Int get() = this
val Int.s: Int get() = this.ms * 1000
val Int.m: Int get() = this.s * 60
val Int.h: Int get() = this.m * 60
val Int.d: Int get() = this.h * 24
