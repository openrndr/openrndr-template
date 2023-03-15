# OPENRNDR template project

A feature rich template for creating OPENRNDR programs based on Gradle/Kts

The template consists of a configuration for Gradle and an example OPENRNDR program. The Gradle configuration should serve as the
go-to starting point for writing OPENRNDR-based software.

If you are looking at this from IntelliJ IDEA you can start by expanding the _project_ tab on the left. You will find a template program in `src/main/kotlin/TemplateProgram.kt`

You will find some [basic instructions](https://guide.openrndr.org/setUpYourFirstProgram.html) in the [OPENRNDR guide](https://guide.openrndr.org)

## Gradle tasks

 - `run` runs the TemplateProgram
 - `jar` creates an executable platform specific jar file with all dependencies
 - `jpackageZip` creates a zip with a stand-alone executable for the current platform (works with Java 14 only)

## Cross builds

To create runnable jars for a platform different from the platform you use to build one uses `./gradlew jar --PtargetPlatform=<platform>`. The supported platforms are `windows`, `macos`, `linux-x64` and `linux-arm64`. 

## Updating OPENRNDR, ORX and other dependencies

The openrndr-template depends on various packages including the core [openrndr](https://github.com/openrndr/openrndr/) and the [orx](https://github.com/openrndr/orx/) extensions.

The version numbers of these dependencies are specified in the [libs.versions.toml](gradle/libs.versions.toml) file. If you want to learn about file format visit the [Gradle documentation](https://docs.gradle.org/current/userguide/platforms.html#sub:conventional-dependencies-toml) website.

Newer versions of OPENRNDR and ORX bring useful features and bug fixes. The most recent versions are
![openrndr version](https://maven-badges.herokuapp.com/maven-central/org.openrndr/openrndr/badge.svg) (openrndr) and 
![orx version](https://maven-badges.herokuapp.com/maven-central/org.openrndr/openrndr/badge.svg) (orx). To use these versions update the toml file. For example:

    openrndr = "0.4.3-alpha3"
    orx = "0.4.3-alpha3"

You can add other dependencies your project may need to the [build.gradle.kts](https://github.com/openrndr/openrndr-template/blob/16eb227741de1a4062a6e19c7c2b3761a62d4602/build.gradle.kts#L102) file. 

Remember to reload the Gradle configuration after changing any dependencies.

## Run other Kotlin programs from the command line

By default `./gradlew run` runs a program called `TemplateProgram.kt` but a different one can be provided as an argument:

To run `src/main/kotlin/myProgram.kt`

    ./gradlew -Popenrndr.application=MyProgramKt

To run `src/main/kotlin/foo/bar/myProgram.kt` (assuming `package foo.bar` in myProgram.kt)

    ./gradlew -Popenrndr.application=foo.bar.MyProgramKt

## Github Actions

This repository contains a number of Github Actions in `./github/workflows`. 
The actions enable a basic build run on commit, plus publication actions that are executed when
a commit is tagged with a version number like `v0.*` or `v1.*`.
