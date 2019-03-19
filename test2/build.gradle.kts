plugins {
    kotlin("jvm") version "1.3.21"
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
    testCompile(kotlin("test-junit5"))
    runtime("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    testCompile(kotlin("reflect"))
}
