plugins {
    kotlin("jvm") version "1.3.20"
    application
}

application {
    mainClassName = "me.arkadybazhanov.au.java.hw7.QSortKt"
}

group = "me.arkadybazhanov.au"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    compile(kotlin("stdlib"))
    compile(kotlin("test-junit5"))
    compile("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.1.1")
    testCompile(kotlin("test-junit5"))
    testRuntime("org.junit.jupiter:junit-jupiter-engine:5.3.2")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_11
}
