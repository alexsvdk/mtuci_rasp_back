val graphqlVersion = rootProject.properties["graphql_version"]
val kmongoVersion = rootProject.properties["kmongo_version"]
val koinVersion = rootProject.properties["koin_version"]
val logbackVersion = rootProject.properties["logback_version"]

plugins {
    kotlin("jvm")
    id("ru.mtuci.kotlin-library-conventions")
}

dependencies {
    api("com.expediagroup:graphql-kotlin-federation:$graphqlVersion")
    api("org.litote.kmongo:kmongo:$kmongoVersion")
    api("io.insert-koin:koin-core:$koinVersion")
    api("ch.qos.logback:logback-core:$logbackVersion")
    api("ch.qos.logback:logback-classic:$logbackVersion")

    testImplementation("io.insert-koin:koin-test:$koinVersion")
    testImplementation("io.insert-koin:koin-test-junit5:$koinVersion")
    testImplementation("org.junit.jupiter:junit-jupiter:+")
}

repositories {
    mavenCentral()
}

tasks {
    test {
        useJUnitPlatform()
    }
}