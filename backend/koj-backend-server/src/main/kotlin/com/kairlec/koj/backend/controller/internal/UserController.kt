package com.kairlec.koj.backend.controller.internal

import com.kairlec.koj.backend.config.userIdAttributes
import com.kairlec.koj.backend.controller.PublicUserController
import com.kairlec.koj.backend.service.JwtService
import com.kairlec.koj.backend.service.UserService
import com.kairlec.koj.backend.util.*
import com.kairlec.koj.common.exception.UserHasBeBlockedException
import com.kairlec.koj.dao.repository.UserType
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
class UserController(
    private val userService: UserService,
    private val jwtService: JwtService
) {
    @GetMapping("/self")
    suspend fun getUserInfo(@RequestAttribute(userIdAttributes) userId: Long): RE<PublicUserController.UserVO> {
        val user = userService.getUser(userId)?.let {
            if (it.blocked) {
                throw UserHasBeBlockedException()
            }
            PublicUserController.UserVO(it.id, it.username, it.email, it.type, it.createTime)
        }.sureFound("user has been destroy maybe")
        return user.ok {
            identity(jwtService.generateToken(user.id, UserType.valueOf(user.type)))
        }
    }

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