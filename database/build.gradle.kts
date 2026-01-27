plugins {
    kotlin("jvm")
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    // Add database dependencies here
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}
