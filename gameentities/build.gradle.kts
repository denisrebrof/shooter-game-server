plugins {
    id("java")
    kotlin("kapt") version "1.8.21"
    kotlin("jvm") version "1.7.22"
    id("com.google.devtools.ksp") version "1.8.21-1.0.11"
    kotlin("plugin.serialization") version "1.7.20"
}

group = "com.denisrebrof"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation(libs.arrowCore)
    implementation(libs.arrowOptics)
    ksp(libs.arrowOpticsPlugin)

    testImplementation(platform(libs.junit))
    testImplementation(libs.junitJupiter)
}

tasks.test {
    useJUnitPlatform()
}