rootProject.name = "Guilds"

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            // compileOnly dependencies
            library("paper-api", "io.papermc.paper:paper-api:1.21.10-R0.1-SNAPSHOT")
            library("daisylib", "uk.firedev:DaisyLib:2.8.0-SNAPSHOT")
            library("chatchannels", "uk.firedev:ChatChannels:1.0.3")
            library("vault", "net.milkbowl.vault:VaultUnlockedAPI:2.16")

            // implementation dependencies

            // paperLibrary dependencies

            // Gradle plugins
            plugin("shadow", "com.gradleup.shadow").version("9.2.2")
            plugin("plugin-yml", "de.eldoria.plugin-yml.paper").version("0.8.0")
        }
    }
}
