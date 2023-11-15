plugins {
    id("java")
    kotlin("kapt") version "1.8.21"
    kotlin("jvm") version "1.7.22"
    kotlin("plugin.serialization") version "1.7.20"
    id("com.google.devtools.ksp") version "1.8.21-1.0.11"
}

group = "com.denisrebrof"

val arrowVersion = "0.11.0"

repositories {
    mavenCentral()
}

dependencies {
    api(project(":utils"))

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation(libs.kotlinxSerialization)
    implementation(libs.kotlinReflect)

    implementation(libs.rxjava3)

    implementation(libs.arrowCore)
    implementation(libs.arrowOptics)
    ksp(libs.arrowOpticsPlugin)

    testImplementation(platform(libs.junit))
    testImplementation(libs.junitJupiter)
}

tasks.test {
    useJUnitPlatform()
}