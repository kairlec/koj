package com.kairlec.koj.backend.service.impl

import com.kairlec.koj.backend.exp.ResetPasswordCodeWrongException
import com.kairlec.koj.backend.service.MailService
import com.kairlec.koj.backend.service.ResetPasswordMail
import com.kairlec.koj.backend.service.UserService
import com.kairlec.koj.common.InternalApi
import com.kairlec.koj.common.util.digestCharRange
import com.kairlec.koj.common.util.randomString
import com.kairlec.koj.dao.extended.ListCondition
import com.kairlec.koj.dao.model.RankInfo
import com.kairlec.koj.dao.model.UserStat
import com.kairlec.koj.dao.repository.PageData
import com.kairlec.koj.dao.repository.UserRepository
import com.kairlec.koj.dao.repository.UserType
import com.kairlec.koj.dao.tables.records.UserRecord
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.beans.factory.DisposableBean
import org.springframework.data.redis.core.ReactiveRedisOperations
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val mailService: MailService,
    private val redisOperations: ReactiveRedisOperations<String, String>
) : UserService, DisposableBean {

    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    suspend fun getResetPasswordCode(address: String): String {
        return redisOperations.opsForValue().get("RESET_PWD_REQ:${address}").awaitSingle()
    }

    suspend fun clearResetPasswordCode(address: String) {
        redisOperations.delete("RESET_PWD_REQ:${address}").awaitSingle()
    }

    suspend fun setResetPasswordCode(address: String, code: String = randomString(8, digestCharRange)): String {
        redisOperations.opsForValue().set("RESET_PWD_REQ:${address}", code, Duration.ofMinutes(10)).awaitSingle()
        return code
    }

    override suspend fun resetPasswordRequest(username: String, email: String) {
        userRepository.get(username = username, email = email) ?: return
        coroutineScope.launch {
            val code = setResetPasswordCode(email)
            mailService.sendMail(email, "[KOJ]重置密码", "你的验证码是：$code , 有效期为10分钟", ResetPasswordMail)
        }
    }

    override suspend fun resetPassword(username: String, email: String, newPwd: String, code: String): Boolean {
        val user = userRepository.get(username = username, email = email) ?: return false
        if (getResetPasswordCode(email) != code) {
            throw ResetPasswordCodeWrongException()
        }
        clearResetPasswordCode(email)
        return userRepository.updateUser(
            id = user.id,
            password = newPwd,
            email = null,
            username = null,
            type = null,
            blocked = null
        )
    }

    override suspend fun changePassword(userId: Long, oldPassword: String, newPassword: String): Boolean {
        return userRepository.changePassword(userId, oldPassword, newPassword)
    }

    override suspend fun getUsers(type: UserType?, listCondition: ListCondition): PageData<UserRecord> {
        val data = userRepository.gets(type, listCondition)
        val count = userRepository.getsCount(type, listCondition)
        return PageData(data, count)
    }

    @InternalApi
    override suspend fun existAdminUsers(): Boolean {
        return userRepository.existAdminUser()
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
        blocked: Boolean?,
    ): Boolean {
        return userRepository.updateUser(id, username, password, email, type, blocked)
    }

    override suspend fun stat(username: String): UserStat? {
        return userRepository.stat(username)
    }

    override suspend fun exists(usernameOrEmail: String): Boolean {
        return userRepository.exists(usernameOrEmail)
    }

    override fun rank(max: Int): Flow<RankInfo> {
        return userRepository.rank(max)
    }

    override fun destroy() {
        coroutineScope.cancel()
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