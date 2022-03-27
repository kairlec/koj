package com.kairlec.koj

import com.kairlec.koj.backend.KojBackendApplication
import com.kairlec.koj.backend.service.UserService
import io.kotest.core.spec.style.StringSpec
import io.kotest.extensions.spring.SpringExtension
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(classes = [KojBackendApplication::class])
class UserTest(
    private val userService: UserService
) : StringSpec({
    "user service"{
        userService.userService()
    }
}) {
    override fun extensions() = listOf(SpringExtension)
}