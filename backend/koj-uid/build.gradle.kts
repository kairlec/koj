plugins {
    alias(libs.plugins.spring)
    alias(libs.plugins.spring.dependency)
}

tasks.bootJar {
    enabled = false
}

dependencies {
    implementation(projects.backend.kojDaoCodegen)
    implementation(libs.spring.boot.starter.jooq) {
        exclude(libs.jooq)
    }
    api(libs.bundles.jooq)
    implementation(libs.apache.common.lang3)
    implementation(libs.reactor.kotlin)
}
