plugins {
    kotlin("jvm") version "2.1.21"
    application
}

group = "com.tulskiy.musique"
version = "1.0.0"

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(25)
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.slf4j:slf4j-api:2.0.17")
    runtimeOnly("org.slf4j:slf4j-simple:2.0.17")
    testImplementation(kotlin("test"))
}

application {
    mainClass = "com.tulskiy.musique.core.cli.PlayerCliKt"
}

tasks.test {
    useJUnitPlatform()
}
