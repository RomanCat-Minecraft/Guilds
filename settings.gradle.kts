rootProject.name = "Guilds"

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            // compileOnly dependencies
            library("paper-api", "io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
            library("daisylib", "uk.firedev:DaisyLib:3.0.1-SNAPSHOT")
            library("chatchannels", "uk.firedev:ChatChannels:1.0.4-SNAPSHOT")
            library("vault", "com.github.MilkBowl:VaultAPI:1.7.1")

            // implementation dependencies

            // paperLibrary dependencies

            // Gradle plugins
            plugin("shadow", "com.gradleup.shadow").version("9.2.2")
            plugin("plugin-yml", "de.eldoria.plugin-yml.paper").version("0.8.0")
        }
    }
}
