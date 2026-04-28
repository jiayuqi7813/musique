plugins {
    kotlin("jvm")
    `java-library`
}

java {
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

kotlin {
    jvmToolchain(25)
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
}

tasks.test {
    useJUnitPlatform()
}
