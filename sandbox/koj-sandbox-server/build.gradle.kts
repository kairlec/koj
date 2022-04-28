plugins {
    alias(libs.plugins.spring)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.spring.dependency)
}

tasks.bootJar {
    mainClass.set("com.kairlec.koj.server.sandbox.KOJSandboxApplicationKt")
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
