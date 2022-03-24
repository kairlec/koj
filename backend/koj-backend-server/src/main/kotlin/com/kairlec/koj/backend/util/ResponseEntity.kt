@file:Suppress("NOTHING_TO_INLINE")

package com.kairlec.koj.backend.util

import org.springframework.http.HttpCookie
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAccessor

/**
 * @author : Kairlec
 * @since : 2022/3/8
 **/


inline val X_TOTAL_COUNT get() = "x-total-count"
inline val X_LAST_LOGIN_TIME get() = "x-last-login-time"

typealias RE<T> = ResponseEntity<T>
typealias REHB = ResponseEntity.HeadersBuilder<*>
typealias REHBF = REHB.() -> Unit
typealias REV = ResponseEntity<Void>

inline fun REHB.xLastLoginTime(time: TemporalAccessor, zoneId: ZoneId = ZoneOffset.ofHours(8)) {
    this.header(
        X_LAST_LOGIN_TIME,
        DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.of(LocalDateTime.from(time), zoneId))
    )
}

inline fun REHB.cookie(cookie: HttpCookie) {
    this.header(HttpHeaders.SET_COOKIE, cookie.toString())
}

inline fun REHB.header(name: String, value: Any) {
    this.header(name, value.toString())
}

inline fun REHB.xCount(value: Int) {
    this.header(X_TOTAL_COUNT, value.toString())
}

inline fun <T> T?.status(status: HttpStatus) = RE.status(status).body(this)
inline fun <T> T?.ok() = status(HttpStatus.OK)
inline fun <T> T?.notModified() = status(HttpStatus.NOT_MODIFIED)
inline fun <T> T?.created() = status(HttpStatus.CREATED)
inline fun <T> T?.conflict() = status(HttpStatus.CONFLICT)

inline fun <T> T?.status(status: HttpStatus, block: REHBF) = RE.status(status).apply(block).body(this)
inline fun <T> T?.ok(block: REHBF) = status(HttpStatus.OK, block)
inline fun <T> T?.notModified(block: REHBF) = status(HttpStatus.NOT_MODIFIED, block)
inline fun <T> T?.created(block: REHBF) = status(HttpStatus.CREATED, block)
inline fun <T> T?.conflict(block: REHBF) = status(HttpStatus.CONFLICT, block)

inline fun Any?.voidOk() = voidStatus(HttpStatus.NO_CONTENT)
inline fun voidStatus(status: HttpStatus) = RE.status(status).build<Void>()
inline fun voidOk() = voidStatus(HttpStatus.NO_CONTENT)
inline fun voidNotMidified() = voidStatus(HttpStatus.NOT_MODIFIED)
inline fun voidCreated() = voidStatus(HttpStatus.CREATED)
inline fun voidConflict() = voidStatus(HttpStatus.CONFLICT)

inline fun Any?.voidOk(block: REHBF) = voidStatus(HttpStatus.NO_CONTENT, block)
inline fun voidStatus(status: HttpStatus, block: REHBF) = RE.status(status).apply(block).build<Void>()
inline fun voidOk(block: REHBF) = voidStatus(HttpStatus.NO_CONTENT, block)
inline fun voidNotMidified(block: REHBF) = voidStatus(HttpStatus.NOT_MODIFIED, block)
inline fun voidCreated(block: REHBF) = voidStatus(HttpStatus.CREATED, block)
inline fun voidConflict(block: REHBF) = voidStatus(HttpStatus.CONFLICT, block)
