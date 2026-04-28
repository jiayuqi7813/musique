plugins {
    kotlin("jvm")
    `java-library`
}

// Java 25 is the target production runtime (released Sep 2025).
// The toolchain below uses Java 21 (LTS) because Kotlin 2.1.0 does not yet
// fully support Java 25 bytecode targets. To upgrade, bump the Kotlin plugin
// to 2.2.0+ in the root build.gradle.kts and change all `21` values below to `25`.
val javaTarget = 21

java {
    sourceCompatibility = JavaVersion.toVersion(javaTarget)
    targetCompatibility = JavaVersion.toVersion(javaTarget)
    toolchain {
        languageVersion = JavaLanguageVersion.of(javaTarget)
    }
}

kotlin {
    jvmToolchain(javaTarget)
}

sourceSets {
    main {
        java {
            srcDirs(
                "src/main/java",
                "../dependencies/alacdecoder/src/main/java",
                "../dependencies/cuelib/src/main/java",
                "../dependencies/jaad/src/main/java",
                "../dependencies/jaudiotagger/src/main/java",
                "../dependencies/javaFlacEncoder/src/main/java",
                "../dependencies/javalayer/src/main/java",
                "../dependencies/jflac/src/main/java",
                "../dependencies/jmac/src/main/java",
                "../dependencies/jorbis/src/main/java",
                "../dependencies/tta/src/main/java",
                "../dependencies/vorbis-java/src/main/java",
                "../dependencies/wavpack/src/main/java"
            )
        }
        resources {
            setSrcDirs(listOf(
                "src/main/resources",
                "../dependencies/javalayer/src/main/resources"
            ))
        }
        kotlin {
            srcDirs("src/main/kotlin")
        }
    }
    test {
        java {
            srcDirs("src/test/java")
        }
        kotlin {
            srcDirs("src/test/kotlin")
        }
    }
}

dependencies {
    implementation("commons-configuration:commons-configuration:1.6")
    implementation("commons-logging:commons-logging:1.2")
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    testImplementation("junit:junit:4.13.2")
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine:5.10.0")
}

tasks.test {
    useJUnitPlatform()
}
