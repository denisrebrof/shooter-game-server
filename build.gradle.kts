import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.7.6"
    id("io.spring.dependency-management") version "1.1.0"
    id("com.google.devtools.ksp") version "1.8.21-1.0.11"
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
    mainClass.set("com.denisrebrof.SpringBootTestApplication")
}

allprojects {
    ext {
       set("spring-security.version", "5.8.1")
    }
}

dependencies {
    implementation(project(":utils"))
    implementation(project(":games"))
    implementation(project(":shooter"))
    implementation(project(":matches"))
    implementation(project(":user"))
    implementation(project(":userdata"))
    implementation(project(":commands"))
    implementation(project(":simplestats"))
    implementation(project(":gameresources"))
    implementation(project(":progression"))
    implementation(project(":weapons"))

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
    implementation(libs.rxjava3)

    testImplementation("org.springframework.boot:spring-boot-starter-test")
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
