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
    implementation(project(":utils"))
    implementation(project(":user"))
    implementation(project(":commands"))

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation(libs.springBeans)
    implementation(libs.springContext)
    implementation(libs.springWeb)

    implementation(libs.hibernate)
    implementation(libs.springJpa)

    testImplementation(platform(libs.junit))
    testImplementation(libs.junitJupiter)
}

tasks.test {
    useJUnitPlatform()
}