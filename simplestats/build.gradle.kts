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
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation(libs.springBeans)
    implementation(libs.springContext)
    implementation(libs.springWeb)

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}