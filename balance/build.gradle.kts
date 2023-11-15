plugins {
    id("java")
    kotlin("plugin.spring") version "1.7.22"
    kotlin("plugin.serialization") version "1.7.20"
    kotlin("kapt") version "1.8.21"
    kotlin("jvm") version "1.7.22"
}

group = "com.denisrebrof"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":commands"))
    implementation(project(":user"))
    implementation(libs.kotlinxSerialization)

    implementation(libs.rxjava3)
    implementation(libs.springBeans)
    implementation(libs.springContext)

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    testImplementation(platform(libs.junit))
    testImplementation(libs.junitJupiter)
}

tasks.test {
    useJUnitPlatform()
}