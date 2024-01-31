plugins {
    id("convention-java")
}


dependencies {
    implementation(project(":lib-core"))
    implementation(libs.apache.pdfbox)
    implementation(libs.slf4j.api)
}

