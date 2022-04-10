package com.kairlec.koj.backend.service.impl

import com.kairlec.koj.backend.service.UserService
import com.kairlec.koj.dao.extended.ListCondition
import com.kairlec.koj.dao.model.UserStat
import com.kairlec.koj.dao.repository.UserRepository
import com.kairlec.koj.dao.repository.UserType
import com.kairlec.koj.dao.tables.records.UserRecord
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service

@Service
class UserServiceImpl(
    private val userRepository: UserRepository
) : UserService {
    override fun getUsers(type: UserType?, listCondition: ListCondition): Flow<UserRecord> {
        return userRepository.gets(type, listCondition)
    }

    override suspend fun addUser(username: String, password: String, email: String, type: UserType): Long {
        return userRepository.createUser(username, password, email, type)
    }

    override suspend fun getUser(id: Long): UserRecord? {
        return userRepository.get(id)
    }

    override suspend fun matchUser(usernameOrEmail: String, password: String): UserRecord? {
        return if ('@' in usernameOrEmail) {
            userRepository.get(email = usernameOrEmail, password = password)
        } else {
            userRepository.get(username = usernameOrEmail, password = password)
        }
    }

    override suspend fun removeUser(id: Long): Boolean {
        return userRepository.removeUser(id)
    }

    override suspend fun updateUser(
        id: Long,
        username: String?,
        password: String?,
        email: String?,
        type: UserType?,
    ): Boolean {
        return userRepository.updateUser(id, username, password, email, type)
    }

    override suspend fun stat(username: String): UserStat? {
        return userRepository.stat(username)
    }

    override suspend fun exists(usernameOrEmail: String): Boolean {
        return userRepository.exists(usernameOrEmail)
    }

//    override suspend fun userService() {
//        userRepository.transaction {
//            // new user
//            val a by +user {
//                username = "kairlec"
//                password = "123456"
//                email = "sunfokairlec@gmail.com"
//                type = UserType.ADMIN
//            }
//            println("a=${a}")
//            // get user
//            val b by query("kairlec") { }
//            println("b=${b.awaitSingle()}")
//            // update user
//            val c by user {
//                username = "kairlec"
//                password = "123123"
//                type = UserType.USER
//            }
//            println("c=${c.awaitSingle()}")
//            // get user
//            val d by query("kairlec") {}
//            println("d=${d.awaitSingle()}")
//            // remove user
//            val e by -user {
//                username = "kairlec"
//            }
//            println("e=${e}")
//        }
//    }
}