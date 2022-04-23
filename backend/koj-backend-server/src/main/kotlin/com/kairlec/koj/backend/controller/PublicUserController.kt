package com.kairlec.koj.backend.controller

import com.kairlec.koj.backend.service.JwtService
import com.kairlec.koj.backend.service.UserService
import com.kairlec.koj.backend.util.*
import com.kairlec.koj.common.exception.UserHasBeBlockedException
import com.kairlec.koj.common.exception.UsernameOrPasswordWrongException
import com.kairlec.koj.dao.model.RankInfo
import com.kairlec.koj.dao.model.UserStat
import com.kairlec.koj.dao.repository.UserType
import kotlinx.coroutines.flow.Flow
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import javax.validation.constraints.Email

@RestController
@RequestMapping("/public")
class PublicUserController(
    private val userService: UserService,
    private val jwtService: JwtService,
) {
    data class LoginModel(
        val usernameOrEmail: String,
        val password: String
    )

    data class UserVO(
        val id: Long,
        val username: String,
        val email: String,
        val type: Byte,
        val createTime: LocalDateTime,
    )

    @PostMapping("/users")
    suspend fun login(
        model: LoginModel
    ): RE<UserVO> {
        val user = userService.matchUser(model.usernameOrEmail, model.password)?.let {
            if (it.blocked) {
                throw UserHasBeBlockedException()
            }
            UserVO(it.id, it.username, it.email, it.type, it.createTime)
        } ?: throw UsernameOrPasswordWrongException()
        return user.ok {
            identity(jwtService.generateToken(user.id, UserType.valueOf(user.type)))
        }
    }

    data class RegisterModel(
        val username: String,
        val password: String,
        val email: String,
    )

    @PutMapping("/users")
    suspend fun register(
        model: RegisterModel
    ): Long {
        return userService.addUser(model.username, model.password, model.email, UserType.USER)
    }

    @GetMapping("/users/{username}")
    suspend fun stat(@PathVariable username: String): UserStat {
        return userService.stat(username).sureFound("User<${username}> not found")
    }

    @RequestMapping("/users/{usernameOrEmail}", method = [RequestMethod.HEAD])
    suspend fun exists(@PathVariable usernameOrEmail: String): REV {
        return if (userService.exists(usernameOrEmail)) {
            voidConflict()
        } else {
            voidOk()
        }
    }

    data class ForgePasswordModel(
        val username: String,
        @Email
        val email: String
    )

    @PostMapping("/users/pwd:forget")
    suspend fun forgetPassword(forgePasswordModel: ForgePasswordModel): REV {
        return userService.resetPasswordRequest(forgePasswordModel.username, forgePasswordModel.email).voidOk()
    }

    data class ResetPasswordModel(
        val username: String,
        val email: String,
        val newPwd: String,
        val code: String
    )

    @PostMapping("/users/pwd:reset")
    suspend fun resetPassword(resetPasswordModel: ResetPasswordModel): REV {
        return userService.resetPassword(
            resetPasswordModel.username,
            resetPasswordModel.email,
            resetPasswordModel.newPwd,
            resetPasswordModel.code
        ).voidOk()
    }

    @GetMapping("/users/rank")
    fun getRank(@RequestParam(defaultValue = "20") max: Int): Flow<RankInfo> {
        return userService.rank(max)
    }
}