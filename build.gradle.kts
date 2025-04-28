plugins {
    `java-library`
}

group = "moe.protasis"
version = "1.1.2"
description = "Parent project for YukiMessenger"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(18)) // Adjust if needed
    }
}

tasks.named("build") {
    finalizedBy(":yukimessenger-api:publishToMavenLocal")
    finalizedBy(":yukimessenger-core-bukkit:publishToMavenLocal")
    finalizedBy(":yukimessenger-core-bungeecord:publishToMavenLocal")
    finalizedBy(":yukimessenger-core-velocity:publishToMavenLocal")
}

allprojects {
    repositories {
        mavenCentral()
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
}
