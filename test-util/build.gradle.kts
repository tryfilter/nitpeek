/**
 * This module is for sharing test-related utility components between the other modules of the project.
 */

plugins {
    id("convention-java")
}

group = "nitpeek"
version = "unspecified"

dependencies {
    implementation(libs.junit.api)
    implementation(libs.mockito)
    implementation(libs.mockito.junit)
    runtimeOnly(libs.junit.platform)
}
