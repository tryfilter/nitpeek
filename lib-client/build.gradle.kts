plugins {
    id("convention-java")
}

dependencies {
    implementation(project(":lib-core"))

    // Plugins need to be present on the module path in order to be picked up by the application.
    // Place demo-plugin on the module path for testing, but don't pass that dependency on to the final artifact:
    testRuntimeOnly(project(":demo-plugin"))
}
