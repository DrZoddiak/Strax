import com.github.jengelman.gradle.plugins.shadow.tasks.ConfigureShadowRelocation
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.spongepowered.gradle.plugin.config.PluginLoaders
import org.spongepowered.plugin.metadata.model.PluginDependency

plugins {
    kotlin("jvm") version "1.7.20"
    kotlin("plugin.serialization") version "1.7.20"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("com.google.devtools.ksp") version "1.7.20-1.0.8"
    id("org.spongepowered.gradle.plugin") version "2.1.1"
}

group = "me.zodd"
version = "0.1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val adventureVersion: String = "4.12.0"

dependencies {
    //annotations
    ksp("dev.zacsweers.autoservice:auto-service-ksp:1.0.0")
    implementation("com.google.auto.service:auto-service-annotations:1.0.1")

    //Chat formatting
    implementation("net.kyori:adventure-text-minimessage:$adventureVersion")
    implementation("net.kyori:adventure-text-serializer-plain:$adventureVersion")

    //Kotlin
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))

    //Configuration
    implementation("org.spongepowered:configurate-extra-kotlin:4.1.2")

    //Storage
    implementation("org.mongodb:mongodb-driver-sync:4.8.1")
    implementation("org.litote.kmongo:kmongo:4.8.0")

    //tests
    testImplementation(kotlin("test"))
}

tasks {
    test {
        useTestNG()
    }
}


sponge {
    apiVersion("8.2.0-SNAPSHOT")
    license("CHANGEME")
    loader {
        name(PluginLoaders.JAVA_PLAIN)
        version("1.0")
    }
    plugin("strax") {
        displayName("Strax")
        entrypoint("me.zodd.strax.Strax")
        description("Just testing things...")
        links {
            homepage("https://spongepowered.org")
            source("https://spongepowered.org/source")
            issues("https://spongepowered.org/issues")
        }
        contributor("Spongie") {
            description("Lead Developer")
        }
        dependency("spongeapi") {
            loadOrder(PluginDependency.LoadOrder.AFTER)
            optional(false)
        }
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}


tasks.withType(JavaCompile::class).configureEach {
    options.apply {
        encoding = "utf-8" // Consistent source file encoding
    }
}

tasks.withType(AbstractArchiveTask::class).configureEach {
    isReproducibleFileOrder = true
    isPreserveFileTimestamps = false
}

kotlin {
    sourceSets.main {
        kotlin.srcDir("build/generated/ksp/main/kotlin")
    }
    sourceSets.test {
        kotlin.srcDir("build/generated/ksp/test/kotlin")
    }
}