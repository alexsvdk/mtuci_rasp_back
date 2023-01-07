val graphqlVersion = rootProject.properties["graphql_version"]
val kmongoVersion = rootProject.properties["kmongo_version"]
val koinVersion = rootProject.properties["koin_version"]
val logbackVersion = rootProject.properties["logback_version"]

plugins {
    id("ru.mtuci.kotlin-library-conventions")
    kotlin("jvm")
}

dependencies {
    api("com.expediagroup:graphql-kotlin-federation:$graphqlVersion")
    api("org.litote.kmongo:kmongo:$kmongoVersion")
    api("io.insert-koin:koin-core:$koinVersion")
    api("ch.qos.logback:logback-core:$logbackVersion")
    api("ch.qos.logback:logback-classic:$logbackVersion")
    testApi("io.insert-koin:koin-test:$koinVersion")
}
repositories {
    mavenCentral()
}
