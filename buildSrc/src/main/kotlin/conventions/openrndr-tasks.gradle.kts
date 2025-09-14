package conventions

val applicationMainClass by properties
tasks.register("create executable jar for $applicationMainClass") {
    group = " \uD83E\uDD8C OPENRNDR"
    dependsOn("shadowJar")
}

tasks.register("run $applicationMainClass") {
    group = " \uD83E\uDD8C OPENRNDR"
    dependsOn("run")
}

tasks.register("create standalone executable for $applicationMainClass") {
    group = " \uD83E\uDD8C OPENRNDR"
    dependsOn("jpackageZip")
}

tasks.register("add IDE file scopes") {
    group = " \uD83E\uDD8C OPENRNDR"
    val scopesFolder = File("${project.projectDir}/.idea/scopes")
    scopesFolder.mkdirs()

    val files = listOf(
        "Code" to "file:*.kt||file:*.frag||file:*.vert||file:*.glsl",
        "Text" to "file:*.txt||file:*.md||file:*.xml||file:*.json",
        "Gradle" to "file[*buildSrc*]:*/||file:*gradle.*||file:*.gradle||file:*/gradle-wrapper.properties||file:*.toml",
        "Media" to "file:*.png||file:*.jpg||file:*.dds||file:*.exr||file:*.mp3||file:*.wav||file:*.mp4||file:*.mov||file:*.svg"
    )
    files.forEach { (name, pattern) ->
        val file = File(scopesFolder, "__$name.xml")
        if (!file.exists()) {
            file.writeText(
                """
                    <component name="DependencyValidationManager">
                      <scope name=" ★ $name" pattern="$pattern" />
                    </component>
                    """.trimIndent()
            )
        }
    }
}
