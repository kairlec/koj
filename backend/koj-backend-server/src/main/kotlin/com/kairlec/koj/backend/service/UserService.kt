package com.kairlec.koj.backend.service

import com.kairlec.koj.common.InternalApi
import com.kairlec.koj.dao.extended.ListCondition
import com.kairlec.koj.dao.model.UserStat
import com.kairlec.koj.dao.repository.UserType
import com.kairlec.koj.dao.tables.records.UserRecord
import kotlinx.coroutines.flow.Flow

interface UserService {
    suspend fun resetPasswordRequest(username: String, email: String)
    suspend fun resetPassword(username: String, email: String, newPwd: String, code: String): Boolean

    fun getUsers(type: UserType? = null, listCondition: ListCondition): Flow<UserRecord>

    @InternalApi
    suspend fun existAdminUsers(): Boolean

    suspend fun matchUser(usernameOrEmail: String, password: String): UserRecord?

    suspend fun getUser(id: Long): UserRecord?

    suspend fun removeUser(id: Long): Boolean

    suspend fun addUser(username: String, password: String, email: String, type: UserType): Long

    suspend fun updateUser(id: Long, username: String?, password: String?, email: String?, type: UserType?): Boolean

    suspend fun stat(username: String): UserStat?

    suspend fun exists(usernameOrEmail: String): Boolean
}