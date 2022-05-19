plugins {
    `kotlin-dsl`
}

repositories {
    mavenLocal()
    mavenCentral()
    gradlePluginPortal()
}

dependencies{
    implementation("gradle.plugin.com.github.johnrengelman:shadow:latest.release")
}
