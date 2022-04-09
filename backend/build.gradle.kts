import org.jetbrains.kotlin.konan.properties.Properties

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

application {
    mainClass.set("ru.mtuci.backend.AppKt")
}

task("buildService") {
    val properties = Properties()
    properties.load(File(rootProject.projectDir, "local.properties").reader())

    val mongoUrl = properties.getProperty("mongo_url")
    val serverPath = properties.getProperty("server_path")
    val serverUser = properties.getProperty("server_user")

    val fileTo = File(buildDir, "mtuci-rasp-backend.service")
    val fileFrom = File(projectDir, "deploy/.service")

    val res = fileFrom.readText()
        .replace("%ENV%", "MONGO_URL=$mongoUrl")
        .replace("%JAR%", "$serverPath/jar/backend.jar")
        .replace("%USER%", serverUser)

    if (fileTo.exists()) {
        fileTo.delete()
    }
    fileTo.createNewFile()
    fileTo.writeText(res)
}