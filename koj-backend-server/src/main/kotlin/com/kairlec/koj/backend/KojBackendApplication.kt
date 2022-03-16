package com.kairlec.koj.backend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KojBackendApplication

fun main(args: Array<String>) {
    runApplication<KojBackendApplication>(*args)
}