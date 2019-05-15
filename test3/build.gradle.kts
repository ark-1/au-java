plugins {
    kotlin("jvm") version "1.3.31"
}

group = "me.arkadybazhanov.au"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    compile(kotlin("stdlib"))
//    compile("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.1.1")
    testCompile(kotlin("test-junit5"))
    testRuntime("org.junit.jupiter:junit-jupiter-engine:5.4.2")
    testImplementation("org.junit.jupiter:junit-jupiter:5.4.2")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
