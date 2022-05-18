import com.github.jengelman.gradle.plugins.shadow.transformers.PropertiesFileTransformer
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    alias(libs.plugins.spring)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.spring.dependency)
    kotlin("kapt")
    id("com.github.johnrengelman.shadow")
    application
}

dependencies {
    implementation(projects.common.kojCommon)
    implementation(projects.common.kojStub)
    implementation(projects.backend.kojJudger)
    implementation(projects.backend.kojDao)
    implementation(projects.backend.kojCryptor)
    implementation(libs.bundles.jooq)

    implementation(libs.kotlin.reflect)
    implementation(libs.kotlin.stdlib.jdk8)
    implementation(libs.jackson.module.kotlin)
    implementation(libs.reactor.kotlin)
    implementation(libs.jwt)
    implementation(libs.mail)
    implementation(libs.spring.boot.starter.mail)
    implementation(libs.spring.boot.starter.webflux)
    implementation(libs.spring.boot.starter.r2dbc)
    implementation(libs.spring.boot.starter.redis.reactive)
    implementation(libs.coroutines.reactive)
    implementation(libs.coroutines.reactor)
    implementation(libs.pulsar) {
        exclude("org.apache.pulsar", "pulsar-common")
    }
    implementation(libs.pulsar.admin)
    implementation(libs.reactive.lock)
    runtimeOnly(libs.driver.mysql.r2dbc)
    runtimeOnly(libs.driver.mysql)
    testImplementation(libs.bundles.springTest)
    kapt(libs.spring.boot.starter.processor)
}

kapt {
    arguments {
        arg(
            "org.springframework.boot.configurationprocessor.additionalMetadataLocations",
            "$projectDir/src/main/resources"
        )
    }
}

tasks.withType<BootJar> {
    enabled = false
}

tasks.shadowJar {
    mergeServiceFiles()
    append("META-INF/spring.handlers")
    append("META-INF/spring.schemas")
    append("META-INF/spring.tooling")
    transform(PropertiesFileTransformer::class.java) {
        paths = listOf("META-INF/spring.factories")
        mergeStrategy = "append"
    }
    doLast {
        copy {
            val jarFilePath = "${project.buildDir}/libs/${project.name}-${project.version}.jar"
            val targetPath = "${rootProject.buildDir}/libs/"
            println("$jarFilePath --> $targetPath")
            from(jarFilePath)
            into(targetPath)
            rename { "${project.name}.jar" }
        }
    }
}

application {
    mainClass.set("com.kairlec.koj.backend.KojBackendApplicationKt")
}