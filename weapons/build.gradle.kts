plugins {
    id("java")
    kotlin("jvm")
    kotlin("plugin.serialization") version "1.7.20"
    kotlin("kapt") version "1.8.21"
}

group = "com.denisrebrof"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":user"))
    implementation(project(":utils"))
    implementation(project(":commands"))
    implementation(project(":balance"))
    implementation(project(":progression"))

    implementation(libs.rxjava3)

    implementation(libs.springBeans)
    implementation(libs.springContext)

    implementation(libs.kotlinxSerialization)

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    testImplementation(platform(libs.junit))
    testImplementation(libs.junitJupiter)
}

tasks.test {
    useJUnitPlatform()
}