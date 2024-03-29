plugins {
    id("ru.mtuci.kotlin-application-conventions")
}

dependencies {
    implementation(project(":core"))
    implementation("org.jsoup:jsoup:1.15.3")
    implementation("org.apache.poi:poi:5.2.3")
    implementation("org.apache.poi:poi-ooxml:5.2.3")
    implementation("org.apache.poi:poi-ooxml-full:5.2.3")
    implementation("org.apache.poi:poi-scratchpad:5.2.3")
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

    register<Ru_mtuci_kotlin_build_service_task_gradle.BuildServiceTask>("buildService") {
        serviceDesc.set("MTUCI Parser service")
    }

    jar {
        from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    }
}