plugins {
    id("java")
}

group = "net.bypiramid.nonslipping"
version = "2.0-SNAPSHOT"

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