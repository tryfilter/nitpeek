
import org.gradle.accessors.dm.LibrariesForLibs

// workaround for version catalog not being accessible in precompiled script files
// https://github.com/gradle/gradle/issues/15383#issuecomment-779893192
val libs = the<LibrariesForLibs>()

plugins {
    id("maven-publish")
    id("java")
}

group = "com.nitpeek"
// artifact id defaults to the directory name of each submodule, which is what we want
version = libs.versions.nitpeek.get()

java {
    withSourcesJar()
    withJavadocJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = group.toString()
            version = version.toString()

            from(components["java"])
        }
    }
}