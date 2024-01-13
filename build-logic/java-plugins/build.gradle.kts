plugins {
    `kotlin-dsl`
}

dependencies {

    // workaround for version catalog not being accessible in precompiled script files
    // https://github.com/gradle/gradle/issues/15383#issuecomment-779893192
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}
