plugins {
    id("convention-java")
}


dependencies {
    implementation(project(":lib-core"))
    implementation(libs.apache.pdfbox)
    implementation(libs.slf4j.api)
    implementation(libs.bundles.docx4j)
}

tasks.jar {
    manifest {
        // Ideally, lib-io would be a proper module, especially for encapsulation purposes.
        // Unfortunately, docx4j makes this prohibitively painful to do.
        attributes["Automatic-Module-Name"] = "nitpeek.lib.io"
    }
}
