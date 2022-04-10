package com.kairlec.koj.backend.service

import com.kairlec.koj.dao.repository.UserType

interface JwtService {
    fun generateToken(userId: Long, userType: UserType): String
    fun parseToken(token: String): Pair<Long, UserType>
}