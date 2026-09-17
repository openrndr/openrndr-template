# OPENRNDR template project

A feature rich template for creating OPENRNDR programs based on [Gradle/Kts](https://en.wikipedia.org/wiki/Gradle).

The template consists of a configuration for Gradle and two example OPENRNDR programs. The Gradle configuration should serve as the
go-to starting point for writing OPENRNDR-based software.

If you are looking at this from IntelliJ IDEA you can start by expanding the _project_ tab on the left. You will find a template program in `src/main/kotlin/TemplateProgram.kt` and a live-coding example in `src/main/kotlin/TemplateLiveProgram.kt`.

You will find some [basic instructions](https://guide.openrndr.org/setUpYourFirstProgram.html) in the [OPENRNDR guide](https://guide.openrndr.org).

## Properties

The file [gradle.properties](gradle.properties) holds properties that are used in the build script.

- `openrndr.allowLocalSnapshots` - when set to `true`, enables the use of locally built OPENRNDR and ORX dependencies
  from your local Maven repository (`~/.m2/repository`). This is useful for testing local changes to OPENRNDR or ORX
  before they are published. Default is `false`.
- `openrndr.allowSonatypeSnapshots` - when set to `true`, enables the use of snapshot versions of OPENRNDR and ORX from
  the Sonatype snapshots repository. Snapshots are pre-release versions that may contain the latest features and bug
  fixes but are not yet stable. Default is `false`.

### Listing properties
- `./gradlew properties` prints the values of all properties that are used in the build script.
 - `./gradlew properties --all` prints the values of all properties that are used in the build script and all subprojects.

## Gradle tasks

 - `./gradlew run` runs `TemplateProgram.kt` (Use `gradlew.bat run` under Windows)
 - `./gradlew run -Popenrndr.application=MyProgramKt` runs `src/main/kotlin/myProgram.kt`
 - `./gradlew run -Popenrndr.application=foo.bar.MyProgramKt` runs `src/main/kotlin/foo/bar/myProgram.kt` (assuming `package foo.bar` is used in myProgram.kt)
 - `./gradlew shadowJar` creates an executable platform specific jar file with all dependencies. Run the resulting program by typing `java -jar build/libs/openrndr-template-1.0.0-all.jar` in a terminal from the project root. If your project contains multiple `main` methods, specify which one to run with `java -cp build/libs/openrndr-template-1.0.0-all.jar MyProgramKt`, where `MyProgramKt` can also be `foo.bar.MyProgramKt` if it's in the package `foo.bar`.
 - `./gradlew jpackageZip` creates a zip with a stand-alone executable for the current platform (requires Java 17 or newer). Run it like this: `cd build/jpackage/openrndr-application/ && bin/openrndr-application`.
 - `./gradlew dependencyUpdates` checks whether any dependencies have newer versions.
 - `./gradlew openrndrRelease` switches to the latest release version of OPENRNDR and ORX.
 - `./gradlew openrndrSonatypeSnapshot` switches to the latest snapshot version of OPENRNDR and ORX.
 - `./gradlew openrndrLocalSnapshot` switches to a locally installed snapshot version of OPENRNDR and ORX.

## Tips and issues

See the [wiki](https://github.com/openrndr/openrndr-template/wiki)

## Cross builds

See the [wiki](https://github.com/openrndr/openrndr-template/wiki)

## Updating OPENRNDR, ORX and other dependencies

The openrndr-template depends on various packages including the core [openrndr](https://github.com/openrndr/openrndr/) and the [orx](https://github.com/openrndr/orx/) extensions and
provides the optional [orsl](https://github.com/openrndr/orsl/) shader helper modules.
The version numbers of these dependencies are specified in your [libs.versions.toml](gradle/libs.versions.toml) file. 
Learn more about this file in the [Gradle documentation](https://docs.gradle.org/current/userguide/platforms.html#sub:conventional-dependencies-toml) website.

Newer versions bring useful features and bug fixes. The most recent versions are<br>
![Maven Central Version](https://img.shields.io/maven-central/v/org.openrndr/openrndr-math-jvm?label=OPENRNDR&color=%23FFC0CB) 
![Maven Central Version](https://img.shields.io/maven-central/v/org.openrndr.extra/orx-noise-jvm?label=ORX&color=%23FFC0CB)

You can add other dependencies needed by your project to your [build.gradle.kts](build.gradle.kts) file, inside the `dependencies { }` block. 

⚠️ Remember to reload the Gradle configuration after changing any dependencies.

## Github Actions

This repository contains various Github Actions under `./github/workflows`:

- [build-on-commit.yaml](.github/workflows/build-on-commit.yaml) runs a basic build on every commit, 
which can help detect issues in the source code.

- [publish-binaries.yaml](.github/workflows/publish-binaries.yaml) publishes binaries for Linux, Mac and Windows 
any time a commit is tagged with a version number like `v1.*`. For example, we can create and push a tag with these git commands:
    ```
    git tag -a v1.0.0 -m "v1.0.0"
    git push origin v1.0.0
    ```

    You can follow the progress of the action under the Actions tab in GitHub. Once complete, the executables will appear under the Releases section.

## Building reusable libraries

This template can be used in two modes depending on which plugin is loaded in the `plugins { ... }` section in 
[build.gradle.kts](build.gradle.kts):

1. `id("conventions.distribute-application")`. The default approach. The project is used to create one or more 
    audiovisual programs, which can be built and shared with others or run as installations.
2. `id("conventions.publish-library")`. With this mode, the template becomes a collection of helper classes and functions
    that you or other people can use in other template-based projects. The `publish-library` convention sets up the 
    `maven-publish` plugin, which adds the `publishToMavenLocal` task and a `demo` sourceSet with runtime dependencies 
    set to go. Demos can be placed under `src/demo/kotlin` and launched right away.

When getting started, you probably want to leave this setting in the default mode. Once you start to work on multiple
projects and want to share code across them, it may be convenient to create a library of reusable code.

Assume you have cloned three copies of the `openrndr-template` repo and named them `myLibrary`, `project1` and `project2`.

* In `myLibrary`, `build.gradle.kts` should use `id("conventions.publish-library")` instead of `id("conventions.distribute-application")`.
  * Fill `gradle.properties` with something like
    ```bash
    # Choose a good name for your library
    project.name=mySuperLibrary
    # Choose any group name (no need to use github) 
    project.group=com.github.myUserName
    # Your main branch often starts with main- or master-
    project.version=main-SNAPSHOT
    ```
  * Write reusable helper classes and functions under `src/main/kotlin/...`.
  * Optionally, write runnable OPENRNDR programs demonstrating the use of the helper classes and functions to `src/demo/kotlin/...`.
    A demo is worth 1000 images.
  * Run `./gradlew publishToMavenLocal -Prelease.version=main-SNAPSHOT` to publish your library locally.
* In `project1`, `build.gradle.kts` should use `id("conventions.distribute-application")`
  * Add `implementation("com.github.myUserName:mySuperLibrary:main-SNAPSHOT")` to the `dependencies { ... }` block in `build.gradle.kts`
    (match whatever names you used in `gradle.properties` in `myLibrary`).
  * Reload Gradle.
  * Use your helper classes and functions in programs under `src/main/kotlin/`.
* In `project2`, `build.gradle.kts` should use `id("conventions.distribute-application")`
  * Add `implementation("com.github.myUserName:mySuperLibrary:main-SNAPSHOT")` to the `dependencies { ... }` block in `build.gradle.kts`
    (match whatever names you used in `gradle.properties` in `myLibrary`).
  * Reload Gradle.
  * Use your helper classes and functions in programs under `src/main/kotlin/`.

If you commit and push `myLibrary` online, you can use [jitpack](https://jitpack.io/) to easily make your library 
available to others. More experienced users can publish it to Maven Central.