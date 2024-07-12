plugins {
    id("convention-java")
    id("convention-publish-maven")
}

dependencies {
    implementation(project(":nitpeek-core"))
    implementation(libs.slf4j.api)

    // Plugins need to be present on the module path in order to be picked up by the application.
    // Place demo-plugin on the module path for testing, but don't pass that dependency on to the final artifact:
    testRuntimeOnly(project(":demo-plugin"))
}

