plugins {
    id("java")
    kotlin("kapt") version "1.8.21"
    kotlin("jvm") version "1.7.22"
    id("com.google.devtools.ksp") version "1.8.21-1.0.11"
}

group = "com.denisrebrof"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")

    testImplementation(platform(libs.junit))
    testImplementation(libs.junitJupiter)
}

tasks.test {
    useJUnitPlatform()
}