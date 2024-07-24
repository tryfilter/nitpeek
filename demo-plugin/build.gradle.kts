plugins {
    id("convention-java")
    id("convention-publish-maven")
}

dependencies {
    api(project(":nitpeek-core"))
}

repositories {
    mavenCentral()
}
