val graphqlVersion = rootProject.properties["graphql_version"]

plugins {
    id("ru.mtuci.kotlin-application-conventions")
    id("org.springframework.boot") version "2.7.7"
}

dependencies {
    implementation(project(":core"))
    implementation("com.expediagroup:graphql-kotlin-spring-server:$graphqlVersion")
    implementation("com.expediagroup:graphql-kotlin-federation:$graphqlVersion")
    implementation("com.graphql-java:graphql-java-extended-scalars:20.0")
}

repositories {
    mavenCentral()
}

application {
    mainClass.set("ru.mtuci.backend.AppKt")
}

tasks {
    register<Ru_mtuci_kotlin_build_service_task_gradle.BuildServiceTask>("buildService") {
        serviceDesc.set("MTUCI Backend service")
    }
}