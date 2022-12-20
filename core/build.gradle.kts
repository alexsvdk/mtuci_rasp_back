val graphqlVersion = rootProject.properties["graphql_version"]
val kmongoVersion = rootProject.properties["kmongo_version"]
val koinVersion = rootProject.properties["koin_version"]
val slf4jVersion = rootProject.properties["slf4j_version"]

plugins {
    id("ru.mtuci.kotlin-library-conventions")
    kotlin("jvm")
}

dependencies {
    api("com.expediagroup:graphql-kotlin-federation:$graphqlVersion")
    api("org.litote.kmongo:kmongo:$kmongoVersion")
    api("io.insert-koin:koin-core:$koinVersion")
    api("org.slf4j:slf4j-simple:$slf4jVersion")
    api("org.slf4j:slf4j-api:$slf4jVersion")
    api("org.slf4j:slf4j-impl:$slf4jVersion")
    api("org.apache.logging.log4j:log4j-core:2.19.0")

    testApi("io.insert-koin:koin-test:$koinVersion")
}
repositories {
    mavenCentral()
}
