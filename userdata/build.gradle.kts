plugins {
    id("java")
    kotlin("plugin.spring") version "1.7.22"
    kotlin("kapt") version "1.8.21"
    kotlin("jvm") version "1.7.22"
}

group = "com.denisrebrof"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":user"))
    implementation(project(":balance"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation(libs.rxjava3)
    implementation(libs.springBeans)
    implementation(libs.springContext)

    implementation(libs.hibernate)
    implementation(libs.springJpa)
}