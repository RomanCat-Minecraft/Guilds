import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    `java-library`
    `maven-publish`
    alias(libs.plugins.shadow)
    alias(libs.plugins.plugin.yml)
}

repositories {
    mavenLocal()
    mavenCentral()
    gradlePluginPortal()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.codemc.io/repository/FireML/")
}

dependencies {
    compileOnly(libs.paper.api)
    compileOnly(libs.daisylib)
}

group = "uk.firedev"
version = "1.0-SNAPSHOT"
description = "The guild and claim system for RomanCat"
java.sourceCompatibility = JavaVersion.VERSION_21

paper {
    name = project.name
    version = project.version.toString()
    main = "uk.firedev.guilds.Guilds"
    apiVersion = "1.21.8"
    author = "FireML"
    description = project.description.toString()

    loader = "uk.firedev.guilds.LibraryLoader"
    generateLibrariesJson = true

    serverDependencies {
        register("DaisyLib") {
            required = true
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
    }
}

tasks {
    build {
        dependsOn(shadowJar)
    }
    shadowJar {
        archiveBaseName.set(project.name)
        archiveVersion.set(project.version.toString())
        archiveClassifier.set("")
    }
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
    generatePaperPluginDescription {
        useGoogleMavenCentralProxy()
    }
}
