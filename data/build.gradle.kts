plugins {
    kotlin("jvm")
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    // Add eBird API client dependencies here
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}
