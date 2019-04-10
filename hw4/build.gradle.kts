import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.20"
}

group = "me.arkadybazhanov.au"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    compile(kotlin("stdlib"))
    compile("org.jetbrains.exposed", "exposed", "0.12.2")
    compile("org.xerial", "sqlite-jdbc", "3.25.2")


    testCompile(kotlin("test-junit5"))
    testCompile("com.google.guava", "guava", "27.0.1-jre")
    compile(group = "org.slf4j", name = "slf4j-simple", version = "1.7.25")

}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_11
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions.jvmTarget = "1.8"

val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions.jvmTarget = "1.8"