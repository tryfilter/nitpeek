dependencyResolutionManagement {
    repositories {
        gradlePluginPortal()
    }

    // workaround for version catalog not being accessible in precompiled script files
    // https://github.com/gradle/gradle/issues/15383#issuecomment-779893192
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

include("java-plugins")

