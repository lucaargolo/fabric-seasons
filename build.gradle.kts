plugins {
    id("maven-publish")
    id("net.fabricmc.fabric-loom") version "1.17.5"
    id("org.ajoberstar.grgit") version "5.0.0"
    id("idea")
}

import org.ajoberstar.grgit.Grgit
import org.kohsuke.github.GHReleaseBuilder
import org.kohsuke.github.GitHub

buildscript {
    dependencies {
        classpath("org.kohsuke:github-api:${project.property("github_api_version") as String}")
    }
}

operator fun Project.get(property: String): String {
    return property(property) as String
}

java {
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

version = project["mod_version"]
group = project["maven_group"]

val environment: Map<String, String> = System.getenv()
val releaseName = "${name.split("-").joinToString(" ") { it.replaceFirstChar { char -> if (char.isLowerCase()) char.titlecase() else char.toString() } }} ${(version as String).split("+")[0]}"
val releaseType = (version as String).split("+")[0].split("-").let { if(it.size > 1) if(it[1] == "BETA" || it[1] == "ALPHA") it[1] else "ALPHA" else "RELEASE" }
val projectReleaseType = releaseType
val releaseFile = "${layout.buildDirectory.get().asFile}/libs/${base.archivesName.get()}-${version}.jar"
val cfGameVersion = (version as String).split("+")[1].let{ if(!project["minecraft_version"].contains("-") && project["minecraft_version"].startsWith(it)) project["minecraft_version"] else "$it-Snapshot"}

fun getChangeLog(): String {
    return "A changelog can be found at https://github.com/lucaargolo/$name/commits/"
}

fun getBranch(): String {
    environment["GITHUB_REF"]?.let { branch ->
        return branch.substring(branch.lastIndexOf("/") + 1)
    }
    val grgit = try {
        extensions.getByName("grgit") as Grgit
    }catch (ignored: Exception) {
        return "unknown"
    }
    val branch = grgit.branch.current().name
    return branch.substring(branch.lastIndexOf("/") + 1)
}

loom {
    accessWidenerPath.set(file("src/main/resources/seasons.accesswidener"))
}

repositories {
    maven {
        name = "Fabric"
        url = uri("https://maven.fabricmc.net/")
    }
    maven {
        name = "TerraformersMC"
        url = uri("https://maven.terraformersmc.com/releases")
    }
    maven {
        name = "Shedaniel"
        url = uri("https://maven.shedaniel.me/")
    }
    maven {
        name = "Mezz"
        url = uri("https://maven.blamejared.com/")
    }
    mavenLocal()
}

dependencies {
    minecraft("com.mojang:minecraft:${project["minecraft_version"]}")
    "mappings"(loom.officialMojangMappings())

    "modImplementation"("net.fabricmc:fabric-loader:${project["loader_version"]}")
    "modImplementation"("net.fabricmc.fabric-api:fabric-api:${project["fabric_version"]}")

    "modRuntimeOnly"("com.terraformersmc:modmenu:${project["modmenu_version"]}")
    "modRuntimeOnly"("mezz.jei:jei-26.1.2-fabric:${project["jei_version"]}")
}

configurations.all {
    resolutionStrategy {
        force("net.fabricmc:fabric-loader:${project["loader_version"]}")
    }
}

tasks.processResources {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE

    inputs.property("version", project.version)

    from(sourceSets["main"].resources.srcDirs) {
        include("fabric.mod.json")
        expand(mutableMapOf("version" to project.version))
    }

    from(sourceSets["main"].resources.srcDirs) {
        exclude("fabric.mod.json")
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.release.set(25)
}

java {
    withSourcesJar()
}

tasks.jar {
    from("LICENSE")
}

//Github publishing
tasks.register("github") {
    dependsOn(tasks.named("remapJar"))
    group = "upload"

    onlyIf { environment.containsKey("GITHUB_TOKEN") }

    doLast {
        val github = GitHub.connectUsingOAuth(environment["GITHUB_TOKEN"])
        val repository = github.getRepository(environment["GITHUB_REPOSITORY"])

        val releaseBuilder = GHReleaseBuilder(repository, version as String)
        releaseBuilder.name(releaseName)
        releaseBuilder.body(getChangeLog())
        releaseBuilder.commitish(getBranch())

        val ghRelease = releaseBuilder.create()
        ghRelease.uploadAsset(file(releaseFile), "application/java-archive")
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }

    repositories {
        maven {
            url = uri("https://maven.cafeteria.dev/releases")
            credentials {
                username = environment["MAVEN_USERNAME"]
                password = environment["MAVEN_PASSWORD"]
            }
        }


    }
}

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}