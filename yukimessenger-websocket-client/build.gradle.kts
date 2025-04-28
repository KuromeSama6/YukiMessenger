plugins {
    java
    `maven-publish`
    id("io.freefair.lombok") version "8.4"
}

version = parent?.version!!

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(18))
    }
}

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.dmulloy2.net/repository/public/")
    maven("https://jitpack.io")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    compileOnly("joda-time:joda-time:2.12.5")
    compileOnly("org.projectlombok:lombok:1.18.30")
    compileOnly("moe.protasis:yukicommons-api:1.2.1")
    compileOnly("org.java-websocket:Java-WebSocket:1.5.4")
    implementation("com.google.code.gson:gson:2.13.1")
    implementation(project(":yukimessenger-api"))
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }
}
