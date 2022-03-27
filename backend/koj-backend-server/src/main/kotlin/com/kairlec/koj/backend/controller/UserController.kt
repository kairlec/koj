package com.kairlec.koj.backend.controller

import com.kairlec.koj.backend.service.UserService
import com.kairlec.koj.common.exception.UsernameOrPasswordWrongException
import com.kairlec.koj.dao.repository.UserType
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user")
class UserController(
    private val userService: UserService
) {
    @PostMapping("")
    suspend fun login(
        @RequestParam usernameOrEmail: String,
        @RequestParam password: String,
    ): Byte {
        return userService.matchUser(usernameOrEmail, password)?.type ?: throw UsernameOrPasswordWrongException()
    }

    @PutMapping("")
    suspend fun register(
        @RequestParam username: String,
        @RequestParam password: String,
        @RequestParam email: String,
    ): Long {
        return userService.addUser(username, password, email, UserType.USER)
    }

    @DeleteMapping("")
    suspend fun destory(
        @RequestParam id: Long,
    ) {
        if (!userService.removeUser(id)) {
//            throw NotModifiedException()
        }
    }

}