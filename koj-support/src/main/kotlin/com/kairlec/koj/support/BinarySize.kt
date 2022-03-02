package com.kairlec.koj.support

val Int.B: Long get() = this.toLong()
val Int.KB: Long get() = this.B * 1024
val Int.MB: Long get() = this.B * 1024 * 1024
val Int.GB: Long get() = this.B * 1024 * 1024 * 1024
val Int.TB: Long get() = this.B * 1024 * 1024 * 1024 * 1024
val Int.PB: Long get() = this.B * 1024 * 1024 * 1024 * 1024 * 1024
val Int.EB: Long get() = this.B * 1024 * 1024 * 1024 * 1024 * 1024 * 1024
