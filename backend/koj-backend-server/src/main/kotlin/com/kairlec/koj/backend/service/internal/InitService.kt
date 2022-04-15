package com.kairlec.koj.backend.service.internal

import com.kairlec.koj.backend.service.UserService
import com.kairlec.koj.common.InternalApi
import com.kairlec.koj.dao.repository.UserType
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.springframework.beans.factory.getBean
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service


@Service
@InternalApi
class InitService {
    @EventListener(ApplicationReadyEvent::class)
    fun init(event: ApplicationReadyEvent) {
        val userService = event.applicationContext.getBean<UserService>()
        runBlocking {
            if (!userService.existAdminUsers()) {
                val adminUserId = userService.addUser("admin", "admin", "admin@admin.com", UserType.ADMIN)
                log.info { "Add admin user: $adminUserId" }
            }
        }
    }

    companion object {
        private val log = KotlinLogging.logger { }
    }
}