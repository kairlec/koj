package com.kairlec.koj.backend.service.impl

import com.kairlec.koj.backend.service.UserService
import com.kairlec.koj.dao.repository.UserRepository
import com.kairlec.koj.dao.repository.UserType
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.stereotype.Service

@Service
class UserServiceImpl(
    private val userRepository: UserRepository
) : UserService {
    override suspend fun userService() {
        userRepository.transaction {
            // new user
            val a by +user {
                username = "kairlec"
                password = "123456"
                email = "sunfokairlec@gmail.com"
                type = UserType.ADMIN
            }
            println("a=${a}")
            // get user
            val b by query("kairlec") { }
            println("b=${b.awaitSingle()}")
            // update user
            val c by user {
                username = "kairlec"
                password = "123123"
                type = UserType.USER
            }
            println("c=${c.awaitSingle()}")
            error("asd")
            // get user
            val d by query("kairlec") {}
            println("d=${d.awaitSingle()}")
            // remove user
            val e by -user {
                username = "kairlec"
            }
            println("e=${e}")
        }
    }
}