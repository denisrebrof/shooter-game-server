plugins {
    kotlin("jvm")
}

group = "com.denisrebrof"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform(libs.junit))
    testImplementation(libs.junitJupiter)
}

tasks.test {
    useJUnitPlatform()
}