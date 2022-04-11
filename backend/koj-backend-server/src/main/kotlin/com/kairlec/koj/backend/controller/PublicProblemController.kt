package com.kairlec.koj.backend.controller

import com.kairlec.koj.backend.service.UserService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/public")
class PublicProblemController(
    private val userService: UserService
) {
}