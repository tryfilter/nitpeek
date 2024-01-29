import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.dependencies

// workaround for version catalog not being accessible in precompiled script files
// https://github.com/gradle/gradle/issues/15383#issuecomment-779893192
val libs = the<LibrariesForLibs>()


plugins {
    java
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(libs.versions.java.get()))
}


dependencies {
    testImplementation(libs.junit.api)
    testRuntimeOnly(libs.junit.platform)
    testImplementation(libs.mockito)
    testImplementation(libs.mockito.junit)
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}