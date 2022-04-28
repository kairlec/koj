import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "com.kairlec.koj"
version = "1.0-SNAPSHOT"

plugins {
    alias(libs.plugins.kotlin.jvm) apply false
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
    }
}