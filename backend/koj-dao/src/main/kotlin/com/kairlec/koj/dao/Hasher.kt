package com.kairlec.koj.dao

/**
 * @author : Kairlec
 * @since : 2022/2/24
 **/
interface Hasher {
    fun check(raw: String, hashed: String): Boolean
    fun hash(raw: String): String
}
