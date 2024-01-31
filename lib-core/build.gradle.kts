plugins {
    id("convention-java")
    id("maven-publish")
}

group = "org.example.nitpeek"
version = "0.0"

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
