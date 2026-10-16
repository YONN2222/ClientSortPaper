plugins {
    base
    id("com.modrinth.minotaur") version "2.10.0"
}

val releaseVersion = providers.gradleProperty("releaseVersion").get()
// Stable by default. Only a suffix makes it a pre release:
// 1.2.0-alpha, 1.2.0-alpha12 -> alpha, 1.2.0-pre, 1.2.0-pre3 -> beta
val suffix = releaseVersion.substringAfter('-', "").lowercase()
val stage = when {
    suffix.startsWith("alpha") -> "alpha"
    suffix.startsWith("pre") -> "beta"
    suffix.isEmpty() -> "release"
    else -> throw GradleException("Unknown version suffix '$suffix'. Use -alpha, -alphaN, -pre or -preN.")
}
val jarFile = layout.projectDirectory.file(providers.gradleProperty("jar").get())
val mcVersions = providers.gradleProperty("gameVersions").get().split(",")
val notesFile = providers.gradleProperty("notesFile").orNull
val notes = if (notesFile != null) {
    providers.fileContents(layout.projectDirectory.file(notesFile)).asText.map { it.trim() }
} else {
    providers.fileContents(layout.projectDirectory.file("../CHANGELOG.md")).asText.map { text ->
        val section = text.split(Regex("(?m)^## ")).firstOrNull { it.startsWith("[$releaseVersion]") }
        section?.lines()?.drop(1)?.joinToString("\n")?.trim().orEmpty()
    }
}

modrinth {
    token.set(providers.environmentVariable("MODRINTH_TOKEN"))
    projectId.set("clientsort-paper")
    versionNumber.set(releaseVersion)
    versionType.set(stage)
    uploadFile.set(jarFile.asFile)
    gameVersions.addAll(mcVersions)
    loaders.addAll("bukkit", "spigot", "paper", "purpur", "folia")
    changelog.set(notes)
}
