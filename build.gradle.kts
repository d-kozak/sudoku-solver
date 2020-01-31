import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
    // Apply the Kotlin JVM plugin to add support for Kotlin on the JVM.
    id("org.jetbrains.kotlin.jvm").version("1.3.21")
    // Apply the application plugin to add support for building a CLI application.
    application

    id("com.github.johnrengelman.shadow") version "4.0.4"
}

group = "io.dkozak"
version = "1.0-SNAPSHOT"

repositories {
    // Use jcenter for resolving your dependencies.
    // You can declare any Maven/Ivy/file repository here.
    jcenter()
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

dependencies {
    // Use the Kotlin JDK 8 standard library.
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.0")

    compile("io.github.microutils:kotlin-logging:1.6.22")
    compile("org.slf4j:slf4j-api:1.7.25")
    compile("org.apache.logging.log4j:log4j-core:2.9.1")
    compile("org.apache.logging.log4j:log4j-api:2.9.1")
    compile("org.apache.logging.log4j:log4j-slf4j-impl:2.9.1")

    testCompile("com.willowtreeapps.assertk:assertk-jvm:0.21")
    testCompile("org.junit.jupiter:junit-jupiter-api:5.5.2")
    testRuntime("org.junit.jupiter:junit-jupiter-engine:5.5.2")
}

application {
    // Define the main class for the application.
    mainClassName = "io.dkozak.sudoku.MainKt"
}

