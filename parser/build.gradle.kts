import org.jetbrains.kotlin.konan.properties.Properties

plugins {
    id("ru.mtuci.kotlin-application-conventions")
}

dependencies {
    implementation(project(":core"))
    implementation("org.jsoup:jsoup:1.15.3")
    implementation("org.apache.poi:poi-ooxml:5.2.2")
    implementation("com.sun.mail:javax.mail:1.6.2")
    implementation("org.tukaani:xz:1.9")
    implementation("org.apache.commons:commons-compress:1.21")
}
repositories {
    mavenCentral()
}

application {
    mainClass.set("ru.mtuci.parser.AppKt")
}

tasks {

    val jar = jar {
        dependsOn.addAll(
            listOf(
                "compileJava",
                "compileKotlin",
                "processResources"
            )
        ) // We need this for Gradle optimization to work
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        manifest { attributes(mapOf("Main-Class" to application.mainClass)) } // Provided we set it up in the application plugin configuration
        val sourcesMain = sourceSets.main.get()
        val contents = configurations.runtimeClasspath.get()
            .map { if (it.isDirectory) it else zipTree(it) } +
                sourcesMain.output
        from(contents)
    }

    task("buildService") {
        val properties = Properties()
        properties.load(File(rootProject.projectDir, "local.properties").reader())

        if (!buildDir.exists()) {
            buildDir.mkdirs()
        }

        val mongoUrl = properties.getProperty("mongo_url")
        val serverPath = properties.getProperty("server_path")
        val serverUser = properties.getProperty("server_user")
        val mailUsername = properties.getProperty("mail_username")
        val mailPassword = properties.getProperty("mail_password")

        val fileTo = File(buildDir, "mtuci-rasp-parser.service")
        val fileFrom = File(projectDir, "deploy/.service")

        val res = fileFrom.readText()
            .replace(
                "%ENV%",
                """
                    Environment="MONGO_URL=$mongoUrl"
                    Environment="MAIL_USERNAME=$mailUsername"
                    Environment="MAIL_PASSWORD=$mailPassword"
                    Environment="APP_BASE_PATH=$serverPath"
                """.trimIndent()

            )
            .replace("%JAR%", "$serverPath/jar/parser.jar")
            .replace("%USER%", serverUser)

        if (fileTo.exists()) {
            fileTo.delete()
        }
        fileTo.createNewFile()
        fileTo.writeText(res)
    }
}