package com.kairlec.koj.backend

import com.kairlec.koj.backend.config.PulsarAdminAutoConfiguration
import com.kairlec.koj.backend.config.SandboxConfig
import io.github.majusko.pulsar.producer.ProducerCollector
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.transaction.annotation.EnableTransactionManagement
import reactor.core.publisher.Hooks

@SpringBootApplication(exclude = [DataSourceAutoConfiguration::class])
@ComponentScan(
//    excludeFilters = [
//        ComponentScan.Filter(
//            type = FilterType.ASSIGNABLE_TYPE,
//            value = [ProducerCollector::class, SandboxConfig::class, PulsarAdminAutoConfiguration::class]
//        )
//    ],
    basePackages = ["com.kairlec.koj", "com.baidu.fsg.uid"]
)
@EnableScheduling
@EnableTransactionManagement
class KojBackendApplication


fun main(args: Array<String>) {
    Hooks.onOperatorDebug()
    runApplication<KojBackendApplication>(*args)
}
