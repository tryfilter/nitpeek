
dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

pluginManagement {
    includeBuild("build-logic")
}

rootProject.name = "nitpeek"
include("app")
include("lib-core")
include("lib-client")
include("demo-plugin")
include("lib-io")
include("test-util")
