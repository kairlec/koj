plugins {
    alias(libs.plugins.spring.dependency)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.spring)
}

tasks.bootJar {
    enabled = false
}

dependencies {
    implementation(libs.reflections)
    implementation(libs.spring.security.crypto)
    api(projects.common.kojCommon)
    api(projects.backend.kojUid)
    api(projects.backend.kojDaoCodegen)
    api(libs.bundles.jooq)
    api(libs.reactor.kotlin)
    api(libs.coroutines.reactive)
    api(libs.coroutines.reactor)
    api(libs.spring.boot.starter)
    implementation(libs.spring.boot.starter.jooq){
        exclude(libs.jooq)
    }
}
