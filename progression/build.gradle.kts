plugins {
    kotlin("plugin.spring") version "1.7.22"
    kotlin("plugin.serialization") version "1.7.20"
    kotlin("kapt") version "1.8.21"
    kotlin("jvm")
}

group = "com.denisrebrof"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":utils"))
    implementation(project(":user"))
    implementation(project(":commands"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation(libs.kotlinxSerialization)

    implementation(libs.rxjava3)
    implementation(libs.springBeans)
    implementation(libs.springContext)
}