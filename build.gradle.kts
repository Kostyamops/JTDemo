val group: String by project
val version: String by project
val minecraftVersion: String by project
val jdkVersion: String by project
val kotlinVersion: String by project

project.group = group
project.version = version

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("com.gradleup.shadow")
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
    maven("https://oss.sonatype.org/content/groups/public/") {
        name = "sonatype"
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:$minecraftVersion-R0.1-SNAPSHOT")
    compileOnly("org.jetbrains.kotlin:kotlin-stdlib")
}

kotlin {
    jvmToolchain(jdkVersion.toInt())
}

tasks.shadowJar {
    archiveClassifier = ""
    archiveFileName.set("${project.name}-${project.version}.jar")

    val serverPath = System.getenv("SERVER_PATH")
    if (System.getenv("TEST_PLUGIN_BUILD") != null) {
        if (serverPath != null) {
            destinationDirectory.set(file("$serverPath\\plugins"))
        } else {
            logger.warn("SERVER_PATH property is not set!")
        }
    }
}

tasks.jar {
    finalizedBy("shadowJar")
    enabled = false
}

tasks.processResources {
    val props =
        mapOf(
            "NAME" to project.name,
            "VERSION" to project.version,
            "MINECRAFT_VERSION" to minecraftVersion,
            "KOTLIN_VERSION" to kotlinVersion,
        )
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}
