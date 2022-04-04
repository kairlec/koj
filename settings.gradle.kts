enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            version("protobuf") {
                strictly("[3.19, 3.20[")
                prefer("3.19.4")
            }
            version("kotlin", "1.6.20")
            version("spring", "2.6.+")
            version("pulsar-starter") {
                strictly("[1, 2[")
                prefer("1.1.0")
            }
            version("docker-java") {
                strictly("[3, 4[")
                prefer("3.2.13")
            }
            version("kotest", "5.+")
            version("kotest-ext-spring", "1.1.+")
            version("junit", "5.+")
            version("pb-plugin", "0.8.+")
            version("kotlin-logging", "2.+")
            version("coroutines", "1.6.+")
            version("reflections", "0.10.+")
            version("pulsar", "2.9.+")
            version("jooq", "3.16.+")
            version("jooq-codegen", "7.+")
            version("pgsql", "42.+")
            version("jackson") {
                strictly("2.13.+")
                reject("2.13.2.1")
            }

            // https://mvnrepository.com/artifact/org.apache.commons/commons-lang3
            version("apache-common-lang3", "3.+")

            library("apache-common-lang3", "org.apache.commons", "commons-lang3").versionRef("apache-common-lang3")

            library("reflections", "org.reflections", "reflections").versionRef("reflections")

            library("docker-java", "com.github.docker-java", "docker-java").versionRef("docker-java")
            library(
                "docker-java-transport-httpclient5",
                "com.github.docker-java",
                "docker-java-transport-httpclient5"
            ).versionRef("docker-java")

            bundle("docker", listOf("docker-java", "docker-java-transport-httpclient5"))

            library("protobuf-java", "com.google.protobuf", "protobuf-java").versionRef("protobuf")
            library("protobuf-kotlin", "com.google.protobuf", "protobuf-kotlin").versionRef("protobuf")
            library("protobuf-java-util", "com.google.protobuf", "protobuf-java-util").versionRef("protobuf")

            bundle("protobuf", listOf("protobuf-java", "protobuf-kotlin", "protobuf-java-util"))

            library("pulsar", "io.github.majusko", "pulsar-java-spring-boot-starter").versionRef("pulsar-starter")
            library("pulsar.admin", "org.apache.pulsar", "pulsar-client-admin").versionRef("pulsar")
            library(
                "coroutines-reactive",
                "org.jetbrains.kotlinx",
                "kotlinx-coroutines-reactive"
            ).versionRef("coroutines")
            library(
                "coroutines-reactor",
                "org.jetbrains.kotlinx",
                "kotlinx-coroutines-reactor"
            ).versionRef("coroutines")
            library(
                "coroutines-jdk8",
                "org.jetbrains.kotlinx",
                "kotlinx-coroutines-jdk8"
            ).versionRef("coroutines")
            library("coroutines-core", "org.jetbrains.kotlinx", "kotlinx-coroutines-core").versionRef("coroutines")
            library("coroutines-test", "org.jetbrains.kotlinx", "kotlinx-coroutines-test").versionRef("coroutines")
            library("reactive-lock", "pro.chenggang", "reactive-lock").version("1.0.0-SNAPSHOT")
            library("kotlin-stdlib", "org.jetbrains.kotlin", "kotlin-stdlib").withoutVersion()
            library("kotlin-stdlib-jdk8", "org.jetbrains.kotlin", "kotlin-stdlib-jdk8").withoutVersion()
            library("kotlin-reflect", "org.jetbrains.kotlin", "kotlin-reflect").withoutVersion()
            library("kotlin-logging", "io.github.microutils", "kotlin-logging-jvm").versionRef("kotlin-logging")

            library("jackson.core", "com.fasterxml.jackson.core", "jackson-core").versionRef("jackson")
            library("jackson.databind", "com.fasterxml.jackson.core", "jackson-databind").versionRef("jackson")
            library("jackson.annotations", "com.fasterxml.jackson.core", "jackson-annotations").versionRef("jackson")
            library(
                "jackson.module.kotlin",
                "com.fasterxml.jackson.module",
                "jackson-module-kotlin"
            ).versionRef("jackson")

            bundle(
                "jackson",
                listOf("jackson.core", "jackson.databind", "jackson.annotations", "jackson.module.kotlin")
            )

            library("reactor.kotlin", "io.projectreactor.kotlin", "reactor-kotlin-extensions").withoutVersion()
            library("reactor.test", "io.projectreactor", "reactor-test").withoutVersion()

            library("spring-security-crypto", "org.springframework.security", "spring-security-crypto").withoutVersion()
            library("spring-boot-starter-jooq", "org.springframework.boot", "spring-boot-starter-jooq").withoutVersion()
            library("spring-boot-starter-jdbc", "org.springframework.boot", "spring-boot-starter-jdbc").withoutVersion()
            library("spring-boot-starter-web", "org.springframework.boot", "spring-boot-starter-web").withoutVersion()
            library("spring-boot-starter", "org.springframework.boot", "spring-boot-starter").withoutVersion()
            library("spring-boot-starter-test", "org.springframework.boot", "spring-boot-starter-test").withoutVersion()
            library(
                "spring-boot-starter-webflux",
                "org.springframework.boot",
                "spring-boot-starter-webflux"
            ).withoutVersion()
            library(
                "spring-boot-starter-r2dbc",
                "org.springframework.boot",
                "spring-boot-starter-data-r2dbc"
            ).withoutVersion()
            library(
                "spring-boot-starter-redis-reactive",
                "org.springframework.boot",
                "spring-boot-starter-data-redis-reactive"
            ).withoutVersion()

            library("driver-mysql", "mysql", "mysql-connector-java").withoutVersion()
            library("driver-mysql-r2dbc", "dev.miku", "r2dbc-mysql").version("0.8.2.RELEASE")
            library("driver-pgsql-r2dbc", "io.r2dbc", "r2dbc-postgresql").version("0.8.12.RELEASE")
            library("driver-pgsql", "org.postgresql", "postgresql").versionRef("pgsql")

            bundle("base", listOf("kotlin-stdlib", "kotlin-logging", "coroutines-core", "coroutines-jdk8"))

            library(
                "coroutines-core-jvm",
                "org.jetbrains.kotlinx",
                "kotlinx-coroutines-core-jvm"
            ).versionRef("coroutines")

            library("spring-test", "org.springframework.boot", "spring-boot-starter-test").withoutVersion()
            library("kotest-spring", "io.kotest.extensions", "kotest-extensions-spring").versionRef("kotest-ext-spring")
            library("kotest-assertions", "io.kotest", "kotest-assertions-core").versionRef("kotest")
            library("kotest-junit5", "io.kotest", "kotest-runner-junit5").versionRef("kotest")
            library("kotest-property", "io.kotest", "kotest-property").versionRef("kotest")
            library("junit-enginer", "org.junit.jupiter", "junit-jupiter-engine").versionRef("junit")
            bundle(
                "test",
                listOf(
                    "kotest-junit5",
                    "kotest-assertions",
                    "kotest-property",
                    "junit-enginer",
                    "coroutines-core",
                    "coroutines-core-jvm",
                    "coroutines-test"
                )
            )
            bundle(
                "springTest",
                listOf(
                    "reactor.test",
                    "kotest-spring",
                    "kotest-junit5",
                    "kotest-assertions",
                    "kotest-property",
                    "junit-enginer",
                    "spring-test",
                    "coroutines-core",
                    "coroutines-core-jvm",
                    "coroutines-test"
                )
            )

            library("jooq", "org.jooq", "jooq").versionRef("jooq")
            library("jooq-kotlin", "org.jooq", "jooq-kotlin").versionRef("jooq")

            bundle("jooq", listOf("jooq", "jooq-kotlin"))

            plugin("protobuf", "com.google.protobuf").versionRef("pb-plugin")
            plugin("kotlin-spring", "org.jetbrains.kotlin.plugin.spring").versionRef("kotlin")
            plugin("kotlin-jvm", "org.jetbrains.kotlin.jvm").versionRef("kotlin")
            plugin("kotlin-kapt", "org.jetbrains.kotlin.plugin.kapt").versionRef("kotlin")
            plugin("kotlin-noarg", "org.jetbrains.kotlin.plugin.noarg").versionRef("kotlin")
            plugin("spring", "org.springframework.boot").versionRef("spring")
            plugin("spring.dependency", "io.spring.dependency-management").version("latest.release")
            plugin("jooq.codegen", "nu.studer.jooq").versionRef("jooq.codegen")

        }
    }
}
rootProject.name = "KOJ"
include("common")
include("common:koj-common")
include("common:koj-stub")
include("common:koj-proto")

include("sandbox")
include("sandbox:koj-sandbox")
include("sandbox:koj-support")
include("sandbox:koj-core")
include("sandbox:koj-sandbox-server")

include("backend")
include("backend:koj-backend-server")
include("backend:koj-judger")
include("backend:koj-uid")
include("backend:koj-dao-codegen")
include("backend:koj-dao")
