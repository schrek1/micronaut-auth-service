rootProject.name = "sherdog-auth"

pluginManagement {
    val kotlinVersion: String by settings

    plugins {
        id("org.jetbrains.kotlin.jvm") version kotlinVersion
        id("org.jetbrains.kotlin.kapt") version kotlinVersion
        id("org.jetbrains.kotlin.plugin.allopen") version kotlinVersion
        id("org.jetbrains.kotlin.plugin.jpa") version kotlinVersion
        id("com.github.johnrengelman.shadow") version "7.0.0"
        id("io.micronaut.application") version "1.5.0"
    }
}
