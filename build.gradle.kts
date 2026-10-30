plugins {
    `java-library`
    id("com.gradleup.shadow") version "9.6.1"
}

val pluginName: String by project
val website: String by project
val author: String by project
val paperVersion: String by project

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.opencollab.dev/main/")
    mavenCentral()
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:$paperVersion")
    compileOnly("org.geysermc.floodgate:api:2.2.4-SNAPSHOT")
    implementation("org.bstats:bstats-bukkit:3.2.1")
    implementation("net.kyori:adventure-text-minimessage:4.21.0")
    implementation("net.kyori:adventure-text-serializer-legacy:4.21.0")
    testImplementation("io.papermc.paper:paper-api:26.3.build.226-stable")
    testImplementation("org.mockbukkit.mockbukkit:mockbukkit-v1.21:4.116.3")
    testImplementation(platform("org.junit:junit-bom:5.13.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

java {
    withSourcesJar()
}

tasks {
    withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        options.release = 21
        options.compilerArgs.add("-Xlint:all")
    }
    processResources {
        val props = mapOf(
            "name" to pluginName,
            "version" to project.version,
            "description" to project.property("description"),
            "website" to website,
            "author" to author,
            "bstatsId" to project.property("bstatsId")
        )
        inputs.properties(props)
        filesMatching(listOf("plugin.yml", "metrics.properties")) {
            expand(props)
        }
    }
    jar {
        archiveClassifier.set("plain")
    }
    shadowJar {
        archiveBaseName.set(pluginName)
        archiveClassifier.set("")
        relocate("org.bstats", "com.yonn2222.clientsortpaper.libs.bstats")
        relocate("net.kyori", "com.yonn2222.clientsortpaper.libs.kyori")
        mergeServiceFiles()
        minimize {
            exclude(dependency("net.kyori:.*"))
        }
    }
    build {
        dependsOn(shadowJar)
    }
    test {
        useJUnitPlatform()
        testLogging {
            events("failed")
            exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        }
    }
}
