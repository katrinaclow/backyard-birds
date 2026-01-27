val kotlin_version: String by project
val logback_version: String by project

plugins {
    kotlin("jvm") version "2.3.0"
    id("io.ktor.plugin") version "3.4.0"
}

group = "ca.backyardbirds"
version = "0.0.1"

kotlin {
    jvmToolchain(21)
}
