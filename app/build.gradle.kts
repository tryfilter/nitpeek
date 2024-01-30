plugins {
    application
    id("convention-java")
}

dependencies {
    implementation(project(":lib-core"))
    implementation(project(":lib-client"))
    implementation(project(":lib-io"))
}


application {
    mainModule = "nitpeek.app.main"
    mainClass = "nitpeek.client.console.Main"
}


tasks.startScripts {

    doLast {
        // Hack to add the plugins folder to the module path
        injectPluginsFolderToScript(windowsScript, ";%APP_HOME%\\\\plugins")
        injectPluginsFolderToScript(unixScript, ":\\\$APP_HOME/plugins")

        // Another hack to work around pdfbox not playing nice with java modules (defaultJvmOpts seems broken on both platforms too)
        val allowReflectionOptions = " --add-opens java.base/java.nio=org.apache.pdfbox.io --add-opens java.base/jdk.internal.ref=org.apache.pdfbox.io"
        appendValueForParameter(windowsScript, "DEFAULT_JVM_OPTS", allowReflectionOptions)
        appendValueForQuotedParameter(unixScript, "DEFAULT_JVM_OPTS", allowReflectionOptions)
    }
}

private fun injectPluginsFolderToScript(scriptFile: File, pluginString: String) {
    appendValueForParameter(scriptFile, "MODULE_PATH", pluginString)
}

private fun appendValueForParameter(scriptFile: File, parameterName: String, value: String) {
    scriptFile.writeText(scriptFile.readText().replace(Regex("(?:set)?($parameterName=.*)"), "$1$value"))
}

private fun appendValueForQuotedParameter(scriptFile: File, parameterName: String, value: String) {
    scriptFile.writeText(scriptFile.readText().replace(Regex("(?:set)?($parameterName)=\"(.*)\""), "$1=\"$2$value\""))
}