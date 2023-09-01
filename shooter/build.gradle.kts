plugins {
    id("java")
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
    api(project(":utils"))
    api(project(":gameentities"))
    api(project(":games"))

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation(libs.rxjava3)

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