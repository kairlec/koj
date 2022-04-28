import nu.studer.gradle.jooq.JooqGenerate
import org.jooq.meta.jaxb.Logging

plugins {
    alias(libs.plugins.spring)
    alias(libs.plugins.spring.dependency)
    alias(libs.plugins.jooq.codegen)
}

tasks.bootJar {
    enabled = false
}

dependencies {
    api(libs.reactor.kotlin)
    api(libs.coroutines.reactor)
    api(libs.coroutines.reactive)
    jooqGenerator("mysql:mysql-connector-java:8.0.28")
    jooqGenerator("jakarta.xml.bind:jakarta.xml.bind-api:3.0.1")
    jooqGenerator("org.glassfish.jaxb:jaxb-runtime:3.0.2")
}
val jooq_host: String? by project
val jooq_user: String? by project
val jooq_password: String? by project

jooq {
    version.set("3.16.5")
    configurations {
        create("main") {
            jooqConfiguration.apply {
                logging = Logging.WARN
                jdbc.apply {
                    driver = "com.mysql.cj.jdbc.Driver"
                    url = jooq_host
                    user = jooq_user
                    password = jooq_password
                }
                generator.apply {
                    name = "org.jooq.codegen.DefaultGenerator"
                    database.apply {
                        name = "org.jooq.meta.mysql.MySQLDatabase"
                        inputSchema = "koj"
                        forcedTypes.addAll(
                            listOf(
                                org.jooq.meta.jaxb.ForcedType()
                                    .withName("BOOLEAN")
                                    .withIncludeTypes("""(?i:TINYINT\(1\))""")
                            )
                        )
                    }
                    generate.apply {
                        isDeprecated = false
                        isRecords = true
                        isImmutablePojos = true
                        isFluentSetters = true
                    }
                    target.apply {
                        packageName = "com.kairlec.koj.dao"
                        directory = "build/generated-src/jooq/main"  // default (can be omitted)
                    }
                    strategy.name = "org.jooq.codegen.DefaultGeneratorStrategy"
                }
            }
        }
    }
}

if (project.hasProperty("JOOQ_CACHE")) {
    sourceSets {
        main {
            java {
                setSrcDirs(srcDirs.filter { it.parentFile.name != "jooq" })
            }
            java.srcDirs("src/main/jooq")
        }
    }
    tasks.named<JooqGenerate>("generateJooq") {
        isEnabled = false
    }
} else {
    requireNotNull(jooq_host) { "jooq_host must be set" }
    requireNotNull(jooq_user) { "jooq_user must be set" }
    requireNotNull(jooq_password) { "jooq_password must be set" }
    tasks.named<JooqGenerate>("generateJooq") {
        doFirst {
            delete {
                delete(project.file("src/main/jooq"))
            }
        }

        doLast {
            copy {
                from(outputDir.get().asFile)
                into(project.file("src/main/jooq"))
            }
        }
    }
}
