plugins {
    id("java")
}

group = "net.bypiramid.nonslipping"
version = "git-2.0.1-SNAPSHOT"
description = "Intelligent engineering that identifies your prison in blocks with no escape conditions."

val pluginName: String by extra { "NonSlipping" }
val website: String by extra { "https://github.com/bypiramid/non-slipping" }
val authors: Array<String> by extra { arrayOf("comicxz", "Gabriel Bruck", "unidade", "deleyy") }

tasks.withType<JavaCompile> {
    sourceCompatibility = "1.8"
    options.encoding = "UTF-8"
}

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT") {
        exclude(group = "net.md-5", module = "bungeecord-chat")
    }
    compileOnly("com.github.azbh111:craftbukkit-1.8.8:R")
}

tasks.processResources {
    filesMatching("plugin.yml") {
        expand(project.properties)
    }
}