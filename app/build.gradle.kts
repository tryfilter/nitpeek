plugins {
    application
    id("convention-java")
}

dependencies {
    implementation(project(":lib-core"))
    implementation(project(":lib-client"))
}


application {
    mainModule = "nitpeek.app.main"
    mainClass = "nitpeek.client.console.Main"
}


tasks.startScripts {

    // Hack to add the plugins folder to the module path
    doLast {
        injectPluginsFolderToScript(windowsScript, ";%APP_HOME%\\\\plugins")
        injectPluginsFolderToScript(unixScript, ":\\\$APP_HOME/plugins")
    }
}

private fun injectPluginsFolderToScript(scriptFile: File, pluginString: String) {
    scriptFile.writeText(scriptFile.readText().replace(Regex("(MODULE_PATH=.*)"), "$1$pluginString"))
}