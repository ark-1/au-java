plugins {
    kotlin("jvm") version "1.3.20"
}

group = "me.arkadybazhanov.au"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    compile(kotlin("stdlib"))
    testCompile(kotlin("test-junit5"))
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_11
}
