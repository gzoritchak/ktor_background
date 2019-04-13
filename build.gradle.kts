import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

buildscript {
    val atomicfu_version by extra("0.12.2")

    repositories {
        mavenCentral()
    }
//    dependencies {
//        classpath("org.jetbrains.kotlinx:atomicfu-gradle-plugin:$atomicfu_version")
//    }
}


plugins {
    application
    kotlin("jvm") version "1.3.21"
    id("com.github.johnrengelman.shadow") version "5.0.0"
}


//apply(plugin = "kotlinx-atomicfu")

group = "io.data2viz.flights"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

application {
    mainClassName = "proto.MainKt"
}

tasks.withType<Jar> {
    manifest {
        attributes(
            mapOf(
                "Main-Class" to application.mainClassName
            )
        )
    }
}

tasks.withType<ShadowJar> {
    baseName = "ktor_background"
    classifier = null
    version = null
}

val ktor_version = "1.1.3"
val logback_version = "1.2.3"

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.google.code.gson:gson:2.8.5")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.1.1")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-apache:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
}

