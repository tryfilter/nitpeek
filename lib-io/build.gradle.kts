plugins {
    id("convention-java")
}


dependencies {
    implementation(project(":lib-core"))
    implementation(libs.apache.pdfbox)
}

