package com.kairlec.koj.backend.controller.internal

import com.kairlec.koj.backend.config.userIdAttributes
import com.kairlec.koj.backend.service.UserService
import com.kairlec.koj.backend.util.RE
import com.kairlec.koj.backend.util.voidNotMidified
import com.kairlec.koj.backend.util.voidOk
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.RequestAttribute
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class UserController(
    private val userService: UserService
) {
    @DeleteMapping("")
    suspend fun destroy(
        @RequestAttribute(userIdAttributes) userId: Long
    ): RE<Void> {
        if (!userService.removeUser(userId)) {
            return voidNotMidified()
        }
        return voidOk()
    }
}