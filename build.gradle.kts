val ktor_version: String by project
val kotlin_version: String by project
val kotest_version: String by project
val kotest_extensions_version: String by project
val logback_version: String by project

plugins {
    application
    kotlin("jvm") version "1.6.0"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.6.0"
}

group = "com.krzykrucz"
version = "0.0.1"
application {
    mainClass.set("com.krzykrucz.ApplicationKt")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlin_version-RC")
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-metrics:$ktor_version")
    implementation("io.ktor:ktor-serialization:$ktor_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    testImplementation("io.ktor:ktor-server-tests:$ktor_version")
    testImplementation("io.kotest:kotest-runner-junit5:$kotest_version")
    testImplementation("io.kotest:kotest-assertions-core:$kotest_version")
    testImplementation("io.kotest:kotest-property:$kotest_version")
    testImplementation("io.kotest.extensions:kotest-assertions-ktor:$kotest_extensions_version")
    testImplementation("io.kotest.extensions:kotest-extensions-wiremock:$kotest_extensions_version")
    testImplementation("com.tngtech.archunit:archunit:0.22.0")
}

tasks.withType<Test> {
    useJUnitPlatform()
}