import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("plugin.serialization") version "1.4.21"
    kotlin("jvm") version "1.5.10"
}

group = "me.igor"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}
dependencies {
    implementation("io.ktor:ktor-client-core:1.6.2")
    implementation("io.ktor:ktor-client-cio:1.6.2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.2")

}


tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}