rootProject.name = "spring-boot-test"

include("progression")
include("games")
include("core")
include("utils")
include("shooter")
include("matches")
include("user")
include("userdata")
include("sessions")
include("commands")
include("balance")
include("lobby")
include("simplestats")
include("gameresources")
include("purchases")
include("weapons")

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            library("kotlinReflect", "org.jetbrains.kotlin:kotlin-reflect:1.7.21")
            library("kotlinxSerialization", "org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
            library("rxjava3", "io.reactivex.rxjava3:rxjava:3.1.5")
            library("lombok", "org.projectlombok:lombok:1.18.24")

            //Spring
            val springLibsVersion = "5.3.24"
            val springSecurityVersion = "5.8.1"
            library("springBeans", "org.springframework:spring-beans:$springLibsVersion")
            library("springContext", "org.springframework:spring-context:$springLibsVersion")
            library("springWeb", "org.springframework:spring-web:$springLibsVersion")
            library("springWebsocket", "org.springframework:spring-websocket:$springLibsVersion")
            library("springJpa", "org.springframework.data:spring-data-jpa:2.7.6")
            library("springSecurityCrypto", "org.springframework.security:spring-security-crypto:$springSecurityVersion")

            library("hibernate", "org.hibernate:hibernate-core:5.6.14.Final")

            //Arrow
            val arrowVersion = "1.2.0"
            library("arrowCore", "io.arrow-kt:arrow-core:$arrowVersion")
            library("arrowOptics", "io.arrow-kt:arrow-optics:$arrowVersion")
            library("arrowOpticsReflect", "io.arrow-kt:arrow-optics-reflect:$arrowVersion")
            library("arrowOpticsPlugin", "io.arrow-kt:arrow-optics-ksp-plugin:$arrowVersion")

            //Tests
            library("junit", "org.junit:junit-bom:5.9.1")
            library("junitJupiter", "org.junit.jupiter:junit-jupiter:5.9.1")
        }
    }
}
