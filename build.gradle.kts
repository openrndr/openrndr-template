import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.internal.os.OperatingSystem
import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.panteleyev.jpackage.ImageType

group = "org.openrndr.template"
version = "1.0.0"

val applicationMainClass = "TemplateProgramKt"

/**  ## additional ORX features to be added to this project */
val orxFeatures = setOf<String>(
//  "orx-boofcv",
    "orx-camera",
//  "orx-chataigne",
    "orx-color",
//  "orx-composition",
    "orx-compositor",
//  "orx-compute-graph",
//  "orx-compute-graph-nodes",
    "orx-delegate-magic",
//  "orx-dnk3",
//  "orx-easing",
    "orx-envelopes",
//  "orx-expression-evaluator",
//  "orx-fcurve",
//  "orx-fft",
//  "orx-file-watcher",
    "orx-fx",
//  "orx-git-archiver",
//  "orx-gradient-descent",
    "orx-gui",
//  "orx-hash-grid",
    "orx-image-fit",
//  "orx-integral-image",
//  "orx-interval-tree",
//  "orx-jumpflood",
//  "orx-kdtree",
//  "orx-keyframer",
//  "orx-kinect-v1",
//  "orx-kotlin-parser",
//  "orx-marching-squares",
//  "orx-math",
//  "orx-mesh-generators",
//  "orx-midi",
//  "orx-minim",
    "orx-no-clear",
    "orx-noise",
//  "orx-obj-loader",
    "orx-olive",
//  "orx-osc",
//  "orx-palette",
    "orx-panel",
//  "orx-parameters",
//  "orx-poisson-fill",
//  "orx-property-watchers",
//  "orx-quadtree",
//  "orx-rabbit-control",
//  "orx-realsense2",
//  "orx-runway",
    "orx-shade-styles",
//  "orx-shader-phrases",
    "orx-shapes",
//  "orx-svg",
//  "orx-syphon",
//  "orx-temporal-blur",
//  "orx-tensorflow",
//  "orx-text-writer",
//  "orx-time-operators",
//  "orx-timer",
//  "orx-triangulation",
//  "orx-turtle",
    "orx-video-profiles",
    "orx-view-box",
)

/** ## additional ORML features to be added to this project */
val ormlFeatures = setOf<String>(
//    "orml-blazepose",
//    "orml-dbface",
//    "orml-facemesh",
//    "orml-image-classifier",
//    "orml-psenet",
//    "orml-ssd",
//    "orml-style-transfer",
//    "orml-super-resolution",
//    "orml-u2net",
)

/** ## additional OPENRNDR features to be added to this project */
val openrndrFeatures = setOfNotNull(
    if (DefaultNativePlatform("current").architecture.name != "arm-v8") "video" else null
)

/** ## configure the type of logging this project uses */
enum class Logging { NONE, SIMPLE, FULL }

val applicationLogging = Logging.FULL

// ------------------------------------------------------------------------------------------------------------------ //

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    java
    application
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.shadow)
    alias(libs.plugins.jpackage)
    alias(libs.plugins.gitarchive.tomarkdown).apply(false)
    alias(libs.plugins.versions)
    alias(libs.plugins.kotlin.serialization)
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {

//    implementation(libs.jsoup)
//    implementation(libs.csv)

    /* ORSL dependencies */

//    implementation(libs.orsl.shader.generator)
//    implementation(libs.orsl.extension.color)
//    implementation(libs.orsl.extension.easing)
//    implementation(libs.orsl.extension.gradient)
//    implementation(libs.orsl.extension.noise)
//    implementation(libs.orsl.extension.pbr)
//    implementation(libs.orsl.extension.raymarching)
//    implementation(libs.orsl.extension.sdf)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.serialization.core)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.slf4j.api)
    implementation(libs.kotlin.logging)

    when (applicationLogging) {
        Logging.NONE -> {
            runtimeOnly(libs.slf4j.nop)
        }

        Logging.SIMPLE -> {
            runtimeOnly(libs.slf4j.simple)
        }

        Logging.FULL -> {
            runtimeOnly(libs.log4j.slf4j2)
            runtimeOnly(libs.log4j.core)
            runtimeOnly(libs.jackson.databind)
            runtimeOnly(libs.jackson.json)
        }
    }
    implementation(kotlin("stdlib-jdk8"))
    testImplementation(libs.junit)
}

// ------------------------------------------------------------------------------------------------------------------ //

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
kotlin {
    compilerOptions {
        languageVersion = KotlinVersion.KOTLIN_2_0
        apiVersion = KotlinVersion.KOTLIN_2_0
        jvmTarget = JvmTarget.JVM_17
    }
}

// ------------------------------------------------------------------------------------------------------------------ //

application {
    mainClass = if (hasProperty("openrndr.application"))
        "${property("openrndr.application")}"
    else
        applicationMainClass
}

tasks {
    named<ShadowJar>("shadowJar") {
        manifest {
            attributes["Main-Class"] = applicationMainClass
            attributes["Implementation-Version"] = project.version
        }
        minimize {
            exclude(dependency("org.openrndr:openrndr-gl3:.*"))
            exclude(dependency("org.jetbrains.kotlin:kotlin-reflect:.*"))
            exclude(dependency("org.slf4j:slf4j-simple:.*"))
            exclude(dependency("org.apache.logging.log4j:log4j-slf4j2-impl:.*"))
            exclude(dependency("com.fasterxml.jackson.core:jackson-databind:.*"))
            exclude(dependency("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:.*"))
            exclude(dependency("org.bytedeco:.*"))
        }
    }
}

// ------------------------------------------------------------------------------------------------------------------ //

tasks {
    named("jpackage") {
        doLast {
            val destPath = "build/jpackage/" + if (OperatingSystem.current().isMacOsX)
                "openrndr-application.app/Contents/Resources/data"
            else
                "openrndr-application/data"

            copy {
                from("data") { include("**/*") }
                into(destPath)
            }
        }
    }

    register<Zip>("jpackageZip") {
        archiveFileName = "openrndr-application.zip"
        from("${layout.buildDirectory.get()}/jpackage") {
            include("**/*")
        }
        isPreserveFileTimestamps = true
        isReproducibleFileOrder = true
        useFileSystemPermissions()
        dependsOn("jpackage")
    }
}

// ------------------------------------------------------------------------------------------------------------------ //

tasks.register("copyDependencies", Copy::class) {
    from(configurations.runtimeClasspath).into(layout.buildDirectory.dir("jars"))
}

tasks.register("copyJar", Copy::class) {
    from(tasks.jar).into(layout.buildDirectory.dir("jars"))
}

tasks.jpackage {
    dependsOn("build", "copyDependencies", "copyJar")

    appName = "openrndr-application"
    mac {
        javaOptions = listOf(
            "-XstartOnFirstThread",
            $$"-Duser.dir=$APPDIR/../Resources"
        )
    }
    windows {
    }
    linux {
        type = ImageType.APP_IMAGE
    }
    input = layout.buildDirectory.dir("jars")
    destination = layout.buildDirectory.dir("jpackage")

    mainJar = tasks.jar.get().archiveFileName.get()
    mainClass = applicationMainClass

    addModules = listOf("jdk.unsupported", "java.management", "java.desktop")
    jLinkOptions = listOf("--strip-debug", "--compress", "1", "--no-header-files", "--no-man-pages")
}

// ------------------------------------------------------------------------------------------------------------------ //

tasks.register<org.openrndr.extra.gitarchiver.GitArchiveToMarkdown>("gitArchiveToMarkDown") {
    historySize = 20
}

// ------------------------------------------------------------------------------------------------------------------ //

tasks {
    dependencyUpdates {
        gradleReleaseChannel = "current"

        val nonStableKeywords = listOf("alpha", "beta", "rc")

        fun isNonStable(
            version: String
        ) = nonStableKeywords.any {
            version.lowercase().contains(it)
        }

        rejectVersionIf {
            isNonStable(candidate.version) && !isNonStable(currentVersion)
        }
    }
}

// ------------------------------------------------------------------------------------------------------------------ //

class Openrndr {
    val openrndrVersion = libs.versions.openrndr.get()
    val orxVersion = libs.versions.orx.get()
    val ormlVersion = libs.versions.orml.get()

    // choices are "orx-tensorflow-gpu", "orx-tensorflow"
    val orxTensorflowBackend = "orx-tensorflow"

    val currArch = DefaultNativePlatform("current").architecture.name
    val currOs = OperatingSystem.current()
    val os = if (project.hasProperty("targetPlatform")) {
        val supportedPlatforms = setOf("windows", "macos", "linux-x64", "linux-arm64")
        val platform: String = project.property("targetPlatform") as String
        if (platform !in supportedPlatforms) {
            throw IllegalArgumentException("target platform not supported: $platform")
        } else {
            platform
        }
    } else when {
        currOs.isWindows -> "windows"
        currOs.isMacOsX -> when (currArch) {
            "aarch64", "arm-v8" -> "macos-arm64"
            else -> "macos"
        }

        currOs.isLinux -> when (currArch) {
            "x86-64" -> "linux-x64"
            "aarch64" -> "linux-arm64"
            else -> throw IllegalArgumentException("architecture not supported: $currArch")
        }

        else -> throw IllegalArgumentException("os not supported: ${currOs.name}")
    }

    fun orx(module: String) = "org.openrndr.extra:$module:$orxVersion"
    fun orml(module: String) = "org.openrndr.orml:$module:$ormlVersion"
    fun openrndr(module: String) = "org.openrndr:openrndr-$module:$openrndrVersion"
    fun openrndrNatives(module: String) = "org.openrndr:openrndr-$module-natives-$os:$openrndrVersion"
    fun orxNatives(module: String) = "org.openrndr.extra:$module-natives-$os:$orxVersion"

    init {
        dependencies {
            runtimeOnly(openrndr("gl3"))
            runtimeOnly(openrndrNatives("gl3"))
            implementation(openrndr("openal"))
            runtimeOnly(openrndrNatives("openal"))
            implementation(openrndr("application"))
            implementation(openrndr("animatable"))
            implementation(openrndr("extensions"))
            implementation(openrndr("filter"))
            implementation(openrndr("dialogs"))
            if ("video" in openrndrFeatures) {
                implementation(openrndr("ffmpeg"))
                runtimeOnly(openrndrNatives("ffmpeg"))
            }
            for (feature in orxFeatures) {
                implementation(orx(feature))
            }
            for (feature in ormlFeatures) {
                implementation(orml(feature))
            }
            if ("orx-tensorflow" in orxFeatures) runtimeOnly("org.openrndr.extra:$orxTensorflowBackend-natives-$os:$orxVersion")
            if ("orx-kinect-v1" in orxFeatures) runtimeOnly(orxNatives("orx-kinect-v1"))
            if ("orx-olive" in orxFeatures) implementation(libs.kotlin.script.runtime)
        }
    }
}

val openrndr = Openrndr()

if (properties["openrndr.tasks"] == "true") {
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
}
