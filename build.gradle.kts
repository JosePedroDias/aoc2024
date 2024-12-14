buildscript {
    repositories {
        //mavenCentral()
        //maven("https://mvnrepository.com/artifact/com.microsoft.z3/javaAPI")
        gradlePluginPortal()
    }
    dependencies {
        classpath("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.23.7")
    }
}

apply(plugin = "io.gitlab.arturbosch.detekt")

plugins {
    kotlin("jvm") version "2.1.0"
}

sourceSets {
    main {
        kotlin.srcDir("src")
    }
}

tasks {
    wrapper {
        gradleVersion = "8.11"
    }
}

kotlin {
    compilerOptions {
        //freeCompilerArgs.add("-Xwhen-guards")
    }
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    implementation("tools.aqua:z3-turnkey:4.13.0.1")
}
