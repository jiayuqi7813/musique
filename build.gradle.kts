plugins {
    kotlin("jvm") version "2.1.10"
    java
    application
}

group = "com.tulskiy"
version = "0.4-SNAPSHOT"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jcraft:jorbis:0.0.17")
}

sourceSets {
    named("main") {
        java {
            setSrcDirs(emptyList<String>())
            srcDir("dependencies/alacdecoder/src/main/java")
            srcDir("dependencies/cuelib/src/main/java")
            srcDir("dependencies/jaad/src/main/java")
            srcDir("dependencies/javaFlacEncoder/src/main/java")
            srcDir("dependencies/javalayer/src/main/java")
            srcDir("dependencies/jaudiotagger/src/main/java")
            srcDir("dependencies/jflac/src/main/java")
            srcDir("dependencies/jmac/src/main/java")
            srcDir("dependencies/jorbis/src/main/java")
            srcDir("dependencies/tta/src/main/java")
            srcDir("dependencies/vorbis-java/src/main/java")
            srcDir("dependencies/wavpack/src/main/java")
            srcDir("musique-core/src/main/java")
            exclude("com/tulskiy/musique/gui/**")
            exclude("com/tulskiy/musique/plugins/**")
            exclude("com/tulskiy/musique/spi/**")
            exclude("com/tulskiy/musique/library/**")
            exclude("com/tulskiy/musique/images/**")
            exclude("com/tulskiy/musique/system/Application.java")
            exclude("com/tulskiy/musique/system/Main.java")
            exclude("com/tulskiy/musique/system/DefaultLogFormatter.java")
            exclude("com/tulskiy/musique/system/configuration/**")
            exclude("com/tulskiy/musique/playlist/PlaybackOrder.java")
            exclude("com/tulskiy/musique/playlist/Playlist.java")
            exclude("com/tulskiy/musique/playlist/PlaylistManager.java")
            exclude("com/tulskiy/musique/playlist/Sorter.java")
            exclude("com/tulskiy/musique/playlist/TrackComparator.java")
            exclude("com/tulskiy/musique/playlist/TrackDataCache.java")
            exclude("com/tulskiy/musique/playlist/formatting/**")
            exclude("com/tulskiy/musique/gui/model/FieldValues.java")
            exclude("com/tulskiy/musique/audio/Scrobbler.java")
            exclude("com/tulskiy/musique/audio/Converter.java")
            exclude("com/tulskiy/musique/audio/player/dsp/BeatVis.java")
        }
        kotlin.srcDir("src/main/kotlin")
    }
}

kotlin {
    jvmToolchain(25)
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release = 25
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "com.tulskiy.musique.audio.api.PlayerApiDemoKt"
    }
}

application {
    mainClass.set("com.tulskiy.musique.audio.api.PlayerApiDemoKt")
}
