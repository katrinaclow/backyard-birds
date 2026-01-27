plugins {
    kotlin("jvm")
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    // Add analytics feature dependencies here
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}
