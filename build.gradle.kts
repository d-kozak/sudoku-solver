plugins {
    java
}

group = "io.dkozak"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

tasks.withType<Test> {
    useJUnitPlatform()
}

dependencies {
    testImplementation("org.assertj", "assertj-core", "3.6.1")
    testImplementation("org.junit.jupiter", "junit-jupiter", "5.6.0")
}

