rootProject.name = "spring-boot-test"
include("games")
include("gameentities")
include("core")
include("utils")
include("shooter")

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
            library("springBeans", "org.springframework:spring-beans:$springLibsVersion")
            library("springContext", "org.springframework:spring-context:$springLibsVersion")

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
