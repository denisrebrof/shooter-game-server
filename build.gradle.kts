import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.7.6"
    id("io.spring.dependency-management") version "1.1.0"
    kotlin("plugin.serialization") version "1.7.20"
    kotlin("kapt") version "1.8.21"
    kotlin("jvm") version "1.7.22"
    kotlin("plugin.spring") version "1.7.22"
    kotlin("plugin.jpa") version "1.7.22"
}

group = "com.denisrebrof"

java.sourceCompatibility = JavaVersion.VERSION_17

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

springBoot {
    mainClass.set("com.denisrebrof.springboottest.SpringBootTestApplication")
}

allprojects {
    ext {
       set("spring-security.version", "5.8.1")
    }
}

dependencies {
    implementation(libs.kotlinReflect)

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.security:spring-security-messaging")

    implementation("mysql:mysql-connector-java")

    implementation("javax.servlet:javax.servlet-api:4.0.1")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    runtimeOnly("com.h2database:h2")

    implementation(libs.lombok)
    implementation(libs.kotlinxSerialization)
    implementation(libs.rxjava3)

    testImplementation("org.springframework.boot:spring-boot-starter-test")

    implementation(project(":utils"))
    implementation(project(":games"))
    implementation(project(":gameentities"))
}

//Disable plain jar
tasks.getByName<Jar>("jar") {
    enabled = false
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
