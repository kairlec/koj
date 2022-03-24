package com.kairlec.koj.dao.repository

/**
 * @author : Kairlec
 * @since : 2022/2/11
 **/
interface RepositoryFixedConstant {
    val adminUser: User
}

data class User(
    val username: String,
    val userUid: Long,
)