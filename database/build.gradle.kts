plugins {
    alias(libs.plugins.kotlin.jvm)
    `java-library`
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(project(":domain"))

    // Exposed ORM - exposed-jdbc has Database class needed by consumers
    api(libs.exposed.core)
    api(libs.exposed.jdbc)
    implementation(libs.exposed.java.time)

    // PostgreSQL driver
    implementation(libs.postgresql)

    // Connection pooling
    implementation(libs.hikari)

    // Database migrations
    implementation(libs.flyway.core)
    implementation(libs.flyway.postgresql)

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")

    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation(libs.kotlinx.coroutines.test)
}
