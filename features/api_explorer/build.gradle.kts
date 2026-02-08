plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":database"))

    // Ktor for routing
    implementation(libs.ktor.server.core)

    // Kotlinx serialization
    implementation(libs.kotlinx.serialization.json)

    // Exposed (use same versions as database module via version catalog)
    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.java.time)

    testImplementation("org.jetbrains.kotlin:kotlin-test")
}
