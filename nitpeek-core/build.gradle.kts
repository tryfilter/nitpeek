plugins {
    id("convention-java")
    id("maven-publish")
}

dependencies {
    implementation(libs.slf4j.api)
}

group = "com.nitpeek"
version = libs.versions.nitpeek.get()

java {
    withSourcesJar()
    withJavadocJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = "core"
            version = project.version.toString()

            from(components["java"])
        }
    }
}
