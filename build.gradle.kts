import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "com.kairlec.koj"
version = "1.0-SNAPSHOT"

plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    id("com.github.johnrengelman.shadow")
}

allprojects {
    group = rootProject.group
    version = rootProject.version

    repositories {
        mavenLocal()
        mavenCentral()
    }
}

subprojects {
    if (name.startsWith("koj-") && !name.endsWith("proto")) {
        apply(plugin = "kotlin")
        apply(plugin = "com.github.johnrengelman.shadow")
        dependencies {
            val implementation by configurations
            implementation(rootProject.libs.bundles.base)
        }
        val targetJavaVersion = JavaVersion.VERSION_11
        tasks.withType<KotlinCompile> {
            kotlinOptions {
                freeCompilerArgs = listOf(
                    "-Xjsr305=strict",
                    "-opt-in=kotlin.RequiresOptIn",
                    "-Xcontext-receivers",
                )
                jvmTarget = targetJavaVersion.toString()
                allWarningsAsErrors = true
            }
        }
        tasks.withType<JavaCompile> {
            sourceCompatibility = targetJavaVersion.toString()
            targetCompatibility = targetJavaVersion.toString()
        }
//        tasks.withType<ShadowJar> {
//            archiveBaseName.set(project.name)
//            archiveClassifier.set("")
//        }
    }
}