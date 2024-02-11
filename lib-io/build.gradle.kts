plugins {
    id("convention-java")
    id("org.gradlex.extra-java-module-info").version(libs.versions.extraJavaModuleInfo)
}

repositories {
    maven {
        url = uri("https://plugins.gradle.org/m2/")
    }
}

dependencies {
    implementation(project(":lib-core"))
    implementation(libs.apache.pdfbox)
    implementation(libs.slf4j.api)
    implementation(libs.bundles.docx4j)

    implementation(libs.gradlex.extraJavaModuleInfo)
}

// Workaround for docx4j depending on automatic modules
//
// Specifically, the problem is as follows: docx4j-core requires (transitively!) several old libraries that don't have a
// module-info.java, and don't even declare a stable name in the Automatic-Module-Name of their META-INF/MANIFEST.MF,
// so they become automatic modules with a generated name (Gradle calls these "traditional libraries").
// Gradle out of the box does not support using such traditional libraries in a modularized project (i.e. a project that
// requires its dependencies to be present on the module path - Gradle simply doesn't detect these as modules and thus
// does not add them to the module path: https://github.com/gradle/gradle/issues/17609)
//
// The ideal "solution" would be to have all dependencies of docx4j declare a stable module name, but taking a quick
// glance at the dependencies, this does not seem realistic (at least one of them hasn't had a release in almost 6 years:
// https://github.com/bennidi/mbassador)
extraJavaModuleInfo {
    deriveAutomaticModuleNamesFromFileNames = true
}

