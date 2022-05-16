package com.kairlec.koj.backend.controller.admin

import com.kairlec.koj.backend.service.UserService
import com.kairlec.koj.backend.util.currentListCondition
import com.kairlec.koj.backend.util.sureEffect
import com.kairlec.koj.dao.repository.PageData
import com.kairlec.koj.dao.repository.UserType
import com.kairlec.koj.dao.tables.records.UserRecord
import org.springframework.web.bind.annotation.*

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

    @GetMapping("/-")
    suspend fun list(@RequestParam type: UserType?): PageData<UserRecord> {
        return userService.getUsers(type, currentListCondition())
    }

    @PatchMapping("/{userId}")
    suspend fun updateUser(
        @PathVariable userId: Long,
        @RequestParam username: String?,
        @RequestParam password: String?,
        @RequestParam email: String?,
        @RequestParam type: UserType?,
        @RequestParam blocked: Boolean?
    ) {
        userService.updateUser(userId, username, password, email, type, blocked).sureEffect()
    }
}