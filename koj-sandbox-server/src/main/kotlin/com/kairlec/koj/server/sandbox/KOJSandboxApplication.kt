package com.kairlec.koj.server.sandbox

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KOJSandboxApplication

fun main(args: Array<String>) {
    runApplication<KOJSandboxApplication>(*args)
}