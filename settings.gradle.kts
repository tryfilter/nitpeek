
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
include("nitpeek-core")
include("nitpeek-client")
include("nitpeek-io")
include("demo-plugin")