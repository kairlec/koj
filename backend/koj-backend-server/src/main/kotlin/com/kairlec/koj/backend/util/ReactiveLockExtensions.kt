package com.kairlec.koj.backend.util

import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.mono
import pro.chenggang.project.reactive.lock.core.ReactiveLock

suspend inline fun <T> ReactiveLock.withLock(crossinline block: suspend (Boolean) -> T): T {
    return tryLockThenExecute {
        mono {
            block(it)
        }
    }.awaitSingle()
}