![0337_openrndr-3](https://user-images.githubusercontent.com/983309/70990617-39968280-20c6-11ea-8304-70ad0afa628d.png)
# openrndr-template
A feature rich template for creating OPENRNDR programs based on Gradle/Kts

You will find some [basic instructions](https://guide.openrndr.org/#/02_Getting_Started_with_OPENRNDR/C00_SetupYourFirstProgram) in the [OPENRNDR guide](https://guide.openrndr.org)

The template consists of a configuration for Gradle and an example OPENRNDR program. The Gradle configuration should serve as the
go-to starting point for writing OPENRNDR-based software.

## Gradle tasks
 - `run` runs the TemplateProgram
 - `jar` creates an executable platform specific jar file with all dependencies
 - `zipDistribution` creates a zip file containing the application jar and the data folder
