plugins {
    application
    id("convention-java")
}

dependencies {
    implementation(project(":lib-core"))
}


application {
    mainClass.set("nitpeek.client.demo.DemoStdOutProcessor")
}