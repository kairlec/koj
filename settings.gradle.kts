enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            version("protobuf") {
                strictly("[3.19, 3.20[")
                prefer("3.19.4")
            }
            version("kotlin", "1.6.20-RC")
            version("spring") {
                strictly("[2, 3[")
                prefer("2.6.4")
            }
            version("pulsar-starter") {
                strictly("[1, 2[")
                prefer("1.1.0")
            }
            version("docker-java") {
                strictly("[3, 4[")
                prefer("3.2.13")
            }
            version("pb-plugin", "0.8.+")
            version("kotlin-logging", "2.+")
            version("coroutines", "1.6.+")
            version("reflections", "0.10.+")
            version("pulsar", "2.9.+")
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
            library("coroutines-core", "org.jetbrains.kotlinx", "kotlinx-coroutines-core").versionRef("coroutines")
            library("reactive-lock", "pro.chenggang", "reactive-lock").version("1.0.0-SNAPSHOT")
            library("kotlin-stdlib", "org.jetbrains.kotlin", "kotlin-stdlib").withoutVersion()
            library("kotlin-stdlib-jdk8", "org.jetbrains.kotlin", "kotlin-stdlib-jdk8").withoutVersion()
            library("kotlin-reflect", "org.jetbrains.kotlin", "kotlin-reflect").withoutVersion()
            library("kotlin-logging", "io.github.microutils", "kotlin-logging-jvm").versionRef("kotlin-logging")

            library("jackson.module.kotlin", "com.fasterxml.jackson.module", "jackson-module-kotlin").withoutVersion()
            library("reactor.kotlin", "io.projectreactor.kotlin", "reactor-kotlin-extensions").withoutVersion()
            library("reactor.test", "io.projectreactor", "reactor-test").withoutVersion()

            library("spring-boot-starter-jooq", "org.springframework.boot", "spring-boot-starter-jooq").withoutVersion()
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
            library("driver-mysql-r2dbc", "dev.miku", "r2dbc-mysql").withoutVersion()

            bundle("base", listOf("kotlin-stdlib", "kotlin-logging", "coroutines-core"))

            plugin("protobuf", "com.google.protobuf").versionRef("pb-plugin")
            plugin("kotlin-spring", "org.jetbrains.kotlin.plugin.spring").versionRef("kotlin")
            plugin("kotlin-jvm", "org.jetbrains.kotlin.jvm").versionRef("kotlin")
            plugin("kotlin-kapt", "org.jetbrains.kotlin.kapt").versionRef("kotlin")
            plugin("spring", "org.springframework.boot").versionRef("spring")
            plugin("spring.dependency", "io.spring.dependency-management").version("latest.release")

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
