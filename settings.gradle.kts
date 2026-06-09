pluginManagement {
    repositories {
        maven {
            name = "Fabric"
            url = uri("https://maven.fabricmc.net/")
        }
        maven {
            name = "Gradle Plugins"
            url = uri("https://plugins.gradle.org/m2/")
        }
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "Fabric-Seasons"
