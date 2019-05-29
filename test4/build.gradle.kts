import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.31"
}

group = "me.arkadybazhanov.au"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val junitVersion = "5.3.2"

tasks.withType<Test> {
    useJUnitPlatform()
}

dependencies {
    compile(kotlin("stdlib"))
    compile("no.tornado:tornadofx:1.7.19")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-javafx:1.2.1")
    runtime("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    testCompile(kotlin("test-junit5"))
    testCompile(kotlin("reflect"))
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
