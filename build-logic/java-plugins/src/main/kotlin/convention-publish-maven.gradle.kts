
import org.gradle.accessors.dm.LibrariesForLibs

// workaround for version catalog not being accessible in precompiled script files
// https://github.com/gradle/gradle/issues/15383#issuecomment-779893192
val libs = the<LibrariesForLibs>()

plugins {
    id("maven-publish")
    id("java-library")
}

group = "com.nitpeek"
// artifact id defaults to the directory name of each submodule, which is what we want
// version must be passed when publishing with the -Pversion flag

java {
    withSourcesJar()
    withJavadocJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = group.toString()

            from(components["java"])
        }
    }

    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/tryfilter/test-gradle-publish-monorepo")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}