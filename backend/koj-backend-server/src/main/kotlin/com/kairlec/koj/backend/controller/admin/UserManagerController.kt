package com.kairlec.koj.backend.controller.admin

import com.kairlec.koj.backend.service.UserService
import com.kairlec.koj.backend.util.sureEffect
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/users")
class UserManagerController(
    private val userService: UserService
) {
    @DeleteMapping("/{userId}")
    suspend fun destroy(
        @PathVariable userId: Long
    ) {
        userService.removeUser(userId).sureEffect("delete user failed")
    }
}