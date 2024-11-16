# OPENRNDR template project

A feature rich template for creating OPENRNDR programs based on [Gradle/Kts](https://en.wikipedia.org/wiki/Gradle).

The template consists of a configuration for Gradle and two example OPENRNDR programs. The Gradle configuration should serve as the
go-to starting point for writing OPENRNDR-based software.

If you are looking at this from IntelliJ IDEA you can start by expanding the _project_ tab on the left. You will find a template program in `src/main/kotlin/TemplateProgram.kt` and a live-coding example in `src/main/kotlin/TemplateLiveProgram.kt`.

You will find some [basic instructions](https://guide.openrndr.org/setUpYourFirstProgram.html) in the [OPENRNDR guide](https://guide.openrndr.org)

## Gradle tasks

 - `./gradlew run` runs the TemplateProgram (Use `gradlew.bat run` under Windows)
 - `./gradlew shadowJar` creates an executable platform specific jar file with all dependencies. Run the resulting program by typing `java -jar build/libs/openrndr-template-1.0.0-all.jar` in a terminal from the project root. If your project contains multiple `main` methods, specify which one to run with `java -cp build/libs/openrndr-template-1.0.0-all.jar MyProgramKt`, where `MyProgramKt` can also be `foo.bar.MyProgramKt` if it's in the package `foo.bar`.
 - `./gradlew jpackageZip` creates a zip with a stand-alone executable for the current platform (works with Java 14 only)

## Cross builds

To create a runnable jar for a platform different from your current platform, use `./gradlew jar -PtargetPlatform=<platform>`, where `<platform>` is either `windows`, `macos`, `linux-x64`, or `linux-arm64`. 

## Updating OPENRNDR, ORX and other dependencies

The openrndr-template depends on various packages including the core [openrndr](https://github.com/openrndr/openrndr/) and the [orx](https://github.com/openrndr/orx/) extensions. The version numbers of these dependencies are specified in your [libs.versions.toml](gradle/libs.versions.toml) file. Learn more about this file in the [Gradle documentation](https://docs.gradle.org/current/userguide/platforms.html#sub:conventional-dependencies-toml) website.

Newer versions of OPENRNDR and ORX bring useful features and bug fixes. The most recent versions are
<br>![openrndr version](https://maven-badges.herokuapp.com/maven-central/org.openrndr/openrndr-application/badge.svg) for OPENRNDR. 
<br>![orx version](https://maven-badges.herokuapp.com/maven-central/org.openrndr.extra/orx-parameters-jvm/badge.svg) for ORX.

Switch to the [next-version branch](https://github.com/openrndr/openrndr-template/tree/next-version) or enter these versions manually in your toml file. They can look like "0.4.3" or "0.4.3-alpha4". Use the complete string, as in:

    openrndr = "0.4.3-alpha4"
        orx = "0.4.3-alpha4"

You can add other dependencies needed by your project to your [build.gradle.kts](build.gradle.kts) file, inside the `dependencies { }` block. 

⚠️ Remember to reload the Gradle configuration after changing any dependencies.

## Run other Kotlin programs from the command line

By default `./gradlew run` runs a program called `TemplateProgram.kt` but a different one can be provided as an argument:

To run `src/main/kotlin/myProgram.kt`

    ./gradlew run -Popenrndr.application=MyProgramKt

To run `src/main/kotlin/foo/bar/myProgram.kt` (assuming `package foo.bar` in myProgram.kt)

    ./gradlew run -Popenrndr.application=foo.bar.MyProgramKt

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
