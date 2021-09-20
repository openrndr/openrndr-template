import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

// The name of this project, the default is the template version
// but feel free to change these
group = "org.openrndr.template"
version = "0.4.0"

val applicationMainClass = "TemplateProgramKt"

val openrndrDependencies = OpenrndrDependencies(project)

openrndrDependencies.run {

    //  Additional (ORX) libraries to add to this project.
    orxFeatures = listOf(
        //  "orx-boofcv",
        //  "orx-camera",
        //  "orx-chataigne",
        //  "orx-color",
        "orx-compositor",
        //  "orx-dnk3",
        //  "orx-easing",
        //  "orx-file-watcher",
        //  "orx-filter-extension",
        "orx-fx",
        //  "orx-glslify",
        //  "orx-gradient-descent",
        "orx-gui",
        "orx-image-fit",
        //  "orx-integral-image",
        //  "orx-interval-tree",
        //  "orx-jumpflood",
        //  "orx-kdtree",
        //  "orx-keyframer",
        //  "orx-kinect-v1",
        //  "orx-kotlin-parser",
        //  "orx-mesh-generators",
        //  "orx-midi",
        //  "orx-no-clear",
        "orx-noise",
        //  "orx-obj-loader",
        "orx-olive",
        //  "orx-osc",
        //  "orx-palette",
        "orx-panel",
        //  "orx-parameters",
        //  "orx-poisson-fill",
        //  "orx-rabbit-control",
        //  "orx-realsense2",
        //  "orx-runway",
        "orx-shade-styles",
        //  "orx-shader-phrases",
        //  "orx-shapes",
        //  "orx-syphon",
        //  "orx-temporal-blur",
        //  "orx-tensorflow",
        //  "orx-time-operators",
        //  "orx-timer",
        //  "orx-triangulation",
        //  "orx-video-profiles",
    )

    //  Machine learning models to add to this project.
    ormlFeatures = listOf(
        //  "orml-blazepose",
        //  "orml-dbface",
        //  "orml-facemesh",
        //  "orml-image-classifier",
        //  "orml-psenet",
        //  "orml-ssd",
        //  "orml-style-transfer",
        //  "orml-super-resolution",
        //  "orml-u2net",
    )

    // OPENRNDR libraries to add to this project.
    openrndrFeatures = listOf(
        "video",
    )

    // Choose one of the Tensorflow machine learning backends:
    // "orx-tensorflow-gpu", "orx-tensorflow-mkl", "orx-tensorflow"
    orxTensorflowBackend = "orx-tensorflow-mkl"
}

// For developing OPENRNDR or testing coming features one can:
// 1. Clone the openrndr / orx / orml repositories
// 2. Build them locally
// 3. Activate them:
Versions.run {
    openrndrUseSnapshot = false
    orxUseSnapshot = false
    ormlUseSnapshot = false
}

// Sets how detailed logs should be
val applicationLogging = Logging.Type.FULL

// Not much to see here
plugins {
    java
    kotlin("jvm") version (Versions.kotlin)
    //kotlin("plugin.serialization") version "1.3.70"
    id("com.github.johnrengelman.shadow") version ("6.1.0")
    id("org.beryx.runtime") version ("1.11.4")
}

// Not much to see here
repositories {
    mavenCentral()
    if (Versions.usesSnapshot()) mavenLocal()
    maven(url = "https://maven.openrndr.org")
}

// Add additional (third-party) dependencies here
dependencies {

    // implementation("org.jsoup:jsoup:1.12.2")
    // implementation("com.google.code.gson:gson:2.8.6")

    openrndrDependencies.runtimeOnly().forEach { runtimeOnly(it) }
    openrndrDependencies.implementation().forEach { implementation(it) }
    Logging.runtimeOnly(applicationLogging).forEach { runtimeOnly(it) }

    implementation(kotlin("stdlib-jdk8"))
    testImplementation("junit", "junit", "4.12")
}

// --------------------------------------------------------------------------------------------------------------------

configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

project.setProperty("mainClassName", applicationMainClass)

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }
    named<ShadowJar>("shadowJar") {
        manifest {
            attributes["Main-Class"] = applicationMainClass
        }
        minimize {
            exclude(dependency("org.openrndr:openrndr-gl3:.*"))
            exclude(dependency("org.jetbrains.kotlin:kotlin-reflect:.*"))
        }
    }
    named<org.beryx.runtime.JPackageTask>("jpackage") {
        doLast {
            copy {
                from("data") { include("**/*") }
                into(Paths.jpackageData())
            }
        }
    }
    register<Zip>("jpackageZip") {
        archiveFileName.set("openrndr-application-${OS.getOsString(project)}.zip")
        from("$buildDir/jpackage") {
            include("**/*")
        }
    }
    findByName("jpackageZip")?.dependsOn("jpackage")
}

runtime {
    jpackage {
        imageName = "openrndr-application"
        skipInstaller = true
        if (OS.isMac()) {
            jvmArgs.add("-XstartOnFirstThread")
        }
    }
    options.set(
        setOf(
            "--strip-debug", "--compress", "1",
            "--no-header-files", "--no-man-pages"
        )
    )
    modules.set(setOf("jdk.unsupported", "java.management"))
}
