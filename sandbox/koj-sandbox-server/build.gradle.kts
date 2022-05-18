import com.github.jengelman.gradle.plugins.shadow.transformers.*
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    alias(libs.plugins.spring)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.spring.dependency)
    application
}

application{
    mainClass.set("com.kairlec.koj.server.sandbox.KOJSandboxApplicationKt")
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

dependencies {
    implementation(projects.sandbox.kojCore)
    implementation(projects.sandbox.kojSupport)
    implementation(projects.sandbox.kojSandbox)
    implementation(projects.common.kojCommon)
    implementation(projects.common.kojStub)
    implementation(libs.coroutines.reactive)
    implementation(libs.pulsar)
    implementation(libs.spring.boot.starter)
    testImplementation(libs.spring.boot.starter.test)
}
