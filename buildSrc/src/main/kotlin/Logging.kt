object Logging {
    enum class Type { NONE, SIMPLE, FULL }

    fun runtimeOnly(type: Type) = when (type) {
        Type.NONE -> listOf("org.slf4j:slf4j-nop:1.7.30")
        Type.SIMPLE -> listOf("org.slf4j:slf4j-simple:1.7.30")
        Type.FULL -> listOf(
            "org.apache.logging.log4j:log4j-slf4j-impl:2.13.3",
            "com.fasterxml.jackson.core:jackson-databind:2.11.1",
            "com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.11.1"
        )

    }
}


