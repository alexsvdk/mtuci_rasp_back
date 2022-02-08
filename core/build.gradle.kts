val graphqlVersion = rootProject.properties["graphql_version"]
val kmongoVersion = rootProject.properties["kmongo_version"]
val koinVersion = rootProject.properties["koin_version"]

plugins {
    id("ru.mtuci.kotlin-library-conventions")
}

dependencies {
    api("com.expediagroup:graphql-kotlin-federation:$graphqlVersion")
    api("org.litote.kmongo:kmongo:$kmongoVersion")
    api("io.insert-koin:koin-core:$koinVersion")

    testApi( "io.insert-koin:koin-test:$koinVersion")
}