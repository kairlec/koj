package com.kairlec.koj.backend.controller.admin

import com.kairlec.koj.backend.service.UserService
import com.kairlec.koj.backend.util.RE
import com.kairlec.koj.backend.util.voidNotMidified
import com.kairlec.koj.backend.util.voidOk
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
    ): RE<Void> {
        if (!userService.removeUser(userId)) {
            return voidNotMidified()
        }
        return voidOk()
    }
}