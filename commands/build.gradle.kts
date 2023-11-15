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

sourceSets {
    main {
        java {
            this.setSrcDirs(listOf<String>())
        }
    }
}

dependencies {
    implementation(project(":utils"))
    implementation(libs.rxjava3)

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation(libs.springContext)
    implementation(libs.springBeans)
    implementation(libs.springWebsocket)
}