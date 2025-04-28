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
    implementation("org.java-websocket:Java-WebSocket:1.5.4")
    implementation("com.github.f4b6a3:uuid-creator:5.2.0")
    implementation("commons-io:commons-io:2.14.0")
    compileOnly("org.projectlombok:lombok:1.18.30")
    compileOnly("moe.protasis:yukicommons-api:1.2.1")
    implementation("org.java-websocket:Java-WebSocket:1.5.4")
    implementation("com.github.f4b6a3:uuid-creator:5.2.0")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            groupId = parent?.group as String
            artifactId = "yukimessenger-api"
            version = version.toString()
        }
    }
}


tasks {
    compileJava {
        options.encoding = "UTF-8"
    }
}
