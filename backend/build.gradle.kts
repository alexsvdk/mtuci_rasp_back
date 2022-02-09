val graphqlVersion = rootProject.properties["graphql_version"]

plugins {
    id("ru.mtuci.kotlin-application-conventions")
    id("org.springframework.boot") version "2.3.1.RELEASE"
}

dependencies {
    implementation(project(":core"))
    implementation("com.expediagroup:graphql-kotlin-spring-server:$graphqlVersion")
    implementation("com.expediagroup:graphql-kotlin-federation:$graphqlVersion")
    implementation("com.graphql-java:graphql-java-extended-scalars:17.0")
}
