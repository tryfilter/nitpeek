plugins {
    application
    id("convention-java")
}

dependencies {
    implementation(project(":lib-core"))
    implementation(project(":lib-client"))
}


application {
    mainClass.set("nitpeek.client.demo.DemoStdOutProcessor")
}