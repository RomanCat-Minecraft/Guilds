rootProject.name = "Guilds"

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            // Paper API
            library("paper-api", "io.papermc.paper:paper-api:1.21.8-R0.1-SNAPSHOT")

            // compileOnly dependencies
            library("daisylib", "uk.firedev:DaisyLib:2.6.1-SNAPSHOT")

            // implementation dependencies

            // paperLibrary dependencies

            // Gradle plugins
            plugin("shadow", "com.gradleup.shadow").version("9.2.2")
            plugin("plugin-yml", "de.eldoria.plugin-yml.paper").version("0.8.0")
        }
    }
}
