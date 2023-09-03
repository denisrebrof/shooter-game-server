plugins {
    id("java")
    kotlin("plugin.serialization") version "1.7.20"
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
    implementation(project(":utils"))
    implementation(project(":gameentities"))
    implementation(project(":games"))

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation(libs.kotlinxSerialization)

    implementation(libs.rxjava3)
    implementation(libs.springBeans)
    implementation(libs.springContext)

    implementation(libs.arrowCore)
    implementation(libs.arrowOptics)
    implementation(libs.arrowOpticsReflect)
    ksp(libs.arrowOpticsPlugin)

    testImplementation(platform(libs.junit))
    testImplementation(libs.junitJupiter)
}

tasks.test {
    useJUnitPlatform()
}