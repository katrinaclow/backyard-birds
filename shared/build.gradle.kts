plugins {
    kotlin("jvm")
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    // Add shared logic dependencies here
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}
