plugins {
    kotlin("jvm")
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    // Add domain logic dependencies here
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}
