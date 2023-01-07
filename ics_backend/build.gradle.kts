val ktorVersion = rootProject.properties["ktor_version"].toString()

plugins {
    id("ru.mtuci.kotlin-application-conventions")
    kotlin("jvm")
    id("io.ktor.plugin") version "2.2.2"
}

repositories {
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap") }
}

dependencies {
    implementation(project(":core"))
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-call-logging:$ktorVersion")
    implementation("io.ktor:ktor-server-cors-jvm:$ktorVersion")
    implementation("aws.sdk.kotlin:s3:0.19.2-beta")
    implementation("org.mnode.ical4j:ical4j:4.0.0-beta5")
}

application {
    mainClass.set("ru.mtuci.ics_backend.AppKt")
}

tasks {

    register<Ru_mtuci_kotlin_build_service_task_gradle.BuildServiceTask>("buildService") {
        serviceDesc.set("ICS Backend build service")
        port.set(8081)
    }

}