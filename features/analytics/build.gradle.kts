plugins {
    kotlin("jvm") version "2.3.0"
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    // Add analytics feature dependencies here
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}
