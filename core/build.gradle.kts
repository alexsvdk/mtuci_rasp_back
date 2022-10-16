val graphqlVersion = rootProject.properties["graphql_version"]
val kmongoVersion = rootProject.properties["kmongo_version"]
val koinVersion = rootProject.properties["koin_version"]

plugins {
    id("ru.mtuci.kotlin-library-conventions")
    kotlin("jvm")
}

dependencies {
    api("com.expediagroup:graphql-kotlin-federation:$graphqlVersion")
    api("org.litote.kmongo:kmongo:$kmongoVersion")
    api("io.insert-koin:koin-core:$koinVersion")
    api("org.slf4j:slf4j-simple:2.0.3")

    testApi("io.insert-koin:koin-test:$koinVersion")
}