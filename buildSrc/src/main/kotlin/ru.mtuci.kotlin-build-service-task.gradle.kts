import org.jetbrains.kotlin.konan.properties.Properties

abstract class BuildServiceTask : DefaultTask() {

    @get:Optional
    @get:Input
    abstract val serviceName: Property<String>

    @get:Optional
    @get:Input
    abstract val serviceDesc: Property<String>

    @get:Optional
    @get:Input
    abstract val port: Property<Int>

    @TaskAction
    fun action() {
        val serviceName = this.serviceName.getOrElse(project.name)
        val description = this.serviceDesc.getOrElse("MTUCI SERVICE")
        val port = this.port.getOrElse(8080)
        val project = this.project
        val buildDir = project.buildDir
        val rootProject = project.rootProject

        // init build
        if (!buildDir.exists()) {
            buildDir.mkdirs()
        }

        // load properties
        val properties = Properties()
        properties.load(File(rootProject.projectDir, "local.properties").reader())
        val serverPath = properties.getProperty("server_path")
        val serverUser = properties.getProperty("server_user")

        // load env
        val envFile = Properties()
        envFile.load(File(rootProject.projectDir, "prod.env").reader())
        val envStr = mutableMapOf<String, String>().apply {
            envFile.forEach { key, value -> put(key.toString(), value.toString()) }
            put("PORT", "$port")
        }
            .map { (key, value) -> "Environment=\"$key=$value\"" }
            .joinToString("\n")

        // load service template
        val fileTo = File(buildDir, "mtuci-rasp-${serviceName}.service")
        val fileFrom = File(rootProject.projectDir, "deploy/.service")

        val res = fileFrom.readText()
            .replace("%DESC%", description)
            .replace("%ENV%", envStr)
            .replace("%JAR%", "$serverPath/jar/$serviceName.jar")
            .replace("%USER%", serverUser)

        if (fileTo.exists()) {
            fileTo.delete()
        }
        fileTo.createNewFile()
        fileTo.writeText(res)
    }
}