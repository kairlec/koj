package com.kairlec.koj.backend

import com.kairlec.koj.backend.config.SandboxConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication(exclude = [SandboxConfig::class])
@ComponentScan(
    excludeFilters = [
        ComponentScan.Filter(type = FilterType.REGEX, pattern = ["io.github.majusko.pulsar.producer.ProducerCollector"])
    ]
)
@EnableScheduling
class KojBackendApplication

fun main(args: Array<String>) {
    runApplication<KojBackendApplication>(*args)
}
