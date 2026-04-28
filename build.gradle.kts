// Root project build file - no code here
plugins {
    kotlin("jvm") version "2.1.0" apply false
}

allprojects {
    group = "com.tulskiy.musique"
    version = "0.4.0-SNAPSHOT"

    repositories {
        mavenCentral()
        mavenLocal()
    }
}
