plugins {
    id("java")
    kotlin("kapt") version "1.8.21"
    kotlin("jvm") version "1.7.22"
}

group = "com.denisrebrof"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.rxjava3)

    implementation("org.springframework:spring-beans:5.3.24")

    testImplementation(platform(libs.junit))
    testImplementation(libs.junitJupiter)
}

tasks.test {
    useJUnitPlatform()
}