plugins {
    id("convention-java")
    id("convention-publish-maven")
}

dependencies {
    implementation(project(":nitpeek-core"))
}

repositories {
    mavenCentral()
}
