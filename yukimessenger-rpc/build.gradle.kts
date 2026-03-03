plugins {
    java
    `maven-publish`
    id("io.freefair.lombok") version "8.4"
    id("com.github.johnrengelman.shadow") version "8.1.1"
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
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.dmulloy2.net/repository/public/")
    maven("https://jitpack.io")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    compileOnly("joda-time:joda-time:2.12.5")
    compileOnly("commons-io:commons-io:2.14.0")
    compileOnly("org.projectlombok:lombok:1.18.30")
    compileOnly(project(":yukimessenger-api"))

    // Source: https://mvnrepository.com/artifact/net.bytebuddy/byte-buddy
    implementation("net.bytebuddy:byte-buddy:1.17.8")
}

tasks {
    named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
        archiveClassifier.set("") // Replace default 'all' suffix
        version = project.version.toString()
    }

    build {
        dependsOn(shadowJar)
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            groupId = parent?.group as String
            artifactId = "yukimessenger-rpc"
            version = version.toString()
        }
    }
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }
}
