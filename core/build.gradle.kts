plugins {
    kotlin("jvm")
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    // Add shared utilities here
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}
