import org.jetbrains.kotlin.konan.properties.Properties

val ktorVersion = rootProject.properties["ktor_version"].toString()

plugins {
    id("ru.mtuci.kotlin-application-conventions")
    id("io.ktor.plugin") version "2.1.2"
}

dependencies {
    implementation(project(":core"))
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-routing:$ktorVersion")
}

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
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