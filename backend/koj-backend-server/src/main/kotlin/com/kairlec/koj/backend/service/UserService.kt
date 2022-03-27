package com.kairlec.koj.backend.service

import com.kairlec.koj.dao.extended.ListCondition
import com.kairlec.koj.dao.repository.UserType
import com.kairlec.koj.dao.tables.records.UserRecord
import kotlinx.coroutines.flow.Flow

interface UserService {
    fun getUsers(type: UserType? = null, listCondition: ListCondition): Flow<UserRecord>

    suspend fun matchUser(usernameOrEmail: String, password: String): UserRecord?

    suspend fun getUser(id: Long): UserRecord?

    suspend fun removeUser(id: Long): Boolean

    suspend fun addUser(username: String, password: String, email: String, type: UserType): Long

    suspend fun updateUser(id: Long, username: String?, password: String?, email: String?, type: UserType?): Boolean
}