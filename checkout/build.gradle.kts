import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

group = "com.br.danilo.tcc"
version = "0.0.1"

plugins {
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlinter)
}

buildscript {
    repositories {
        mavenCentral()
    }
}

repositories {
    mavenCentral()
}


dependencies {
    //basics
    implementation(platform(libs.spring.boot.bom))
    implementation(libs.spring.boot.starter)
    implementation(libs.spring.boot.starter.core)


    //web
    implementation(libs.spring.boot.starter.webflux)
    implementation(libs.kotlinx.coroutines.reactor)
    implementation(libs.kotlinx.serialization.json)

    //validation
    implementation(libs.valiktor.core)

    //logging
    implementation(libs.kotlin.logging)

    //database
    implementation(libs.redis.reactive)

    //miscellaneous
    implementation(libs.kotlin.reflect)

    //testing
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.kotest.framework.engine)
    testImplementation(libs.kotest.extensions.spring)
    testImplementation(libs.mockk)

}

tasks{
    jar {
        enabled = false
    }

    compileKotlin {
        compilerOptions {
            apiVersion.set(KotlinVersion.fromVersion(rootProject.libs.versions.compiler.kotlin.get()))
            languageVersion.set(KotlinVersion.fromVersion(rootProject.libs.versions.compiler.kotlin.get()))
            jvmTarget.set(JvmTarget.fromTarget(rootProject.libs.versions.compiler.java.get()))
            freeCompilerArgs.addAll(
                "-Xjdk-release=${rootProject.libs.versions.compiler.java.get()}",
                "-java-parameters",
                "-Xjsr305=strict",
                "-Xjvm-default=all",
                "-opt-in=kotlin.time.ExperimentalTime"
            )
        }
    }

    compileTestKotlin {
        compilerOptions {
            apiVersion.set(KotlinVersion.fromVersion(rootProject.libs.versions.compiler.kotlin.get()))
            languageVersion.set(KotlinVersion.fromVersion(rootProject.libs.versions.compiler.kotlin.get()))
            jvmTarget.set(JvmTarget.fromTarget(rootProject.libs.versions.compiler.java.get()))
            freeCompilerArgs.addAll(
                "-Xjdk-release=${rootProject.libs.versions.compiler.java.get()}",
                "-java-parameters",
                "-Xjsr305=strict",
                "-Xjvm-default=all",
                "-opt-in=kotlin.time.ExperimentalTime"
            )
        }
    }

    test {
        useJUnitPlatform()
        jvmArgs(
            "--add-opens=java.base/java.lang=ALL-UNNAMED",
            "--add-opens=java.base/java.util=ALL-UNNAMED"
        )
    }

    lintKotlinMain {
        setSource("src/main/kotlin")
    }

    lintKotlinTest {
        setSource("src/test/kotlin")
    }
}





