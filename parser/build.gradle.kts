import org.jetbrains.kotlin.konan.properties.Properties

plugins {
    id("ru.mtuci.kotlin-application-conventions")
}

dependencies {
    implementation(project(":core"))
    implementation("org.jsoup:jsoup:1.14.3")
    implementation("org.apache.poi:poi-ooxml:3.11")
}

application {
    mainClass.set("ru.mtuci.parser.AppKt")
}

tasks {

    val jar = jar {
        dependsOn.addAll(listOf("compileJava", "compileKotlin", "processResources")) // We need this for Gradle optimization to work
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        manifest { attributes(mapOf("Main-Class" to application.mainClass)) } // Provided we set it up in the application plugin configuration
        val sourcesMain = sourceSets.main.get()
        val contents = configurations.runtimeClasspath.get()
            .map { if (it.isDirectory) it else zipTree(it) } +
                sourcesMain.output
        from(contents)
    }

    val buildJob = register("buildJob"){
        val properties = Properties()
        properties.load(File(rootProject.projectDir, "local.properties").reader())

        val mongoUrl = properties.getProperty("mongo_url")
        val serverPath = properties.getProperty("server_path")

        val fileTo = File(buildDir, "mtuci-rasp-parser.sh")
        val fileFrom = File(projectDir, "deploy/job.sh")

        val res = fileFrom.readText()
            .replace("%MONGO_URL%", mongoUrl)
            .replace("%JAR%", "$serverPath/jar/parser.jar")

        if (fileTo.exists()) {
            fileTo.delete()
        }
        fileTo.createNewFile()
        fileTo.writeText(res)
    }
}