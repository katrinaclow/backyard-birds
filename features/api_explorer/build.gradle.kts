plugins {
    kotlin("jvm")
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    // Add API explorer feature dependencies here
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}
