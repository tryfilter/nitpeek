plugins {
    application
    id("convention-java")
    id("org.gradlex.extra-java-module-info").version(libs.versions.extraJavaModuleInfo)
}

dependencies {
    implementation(project(":lib-core"))
    implementation(project(":lib-client"))
    implementation(project(":lib-io"))
    implementation(libs.slf4j.api)
    runtimeOnly(libs.log.impl)
    runtimeOnly(libs.log4j.bridge.slf4j)
}


application {
    mainModule = "nitpeek.app.main"
    mainClass = "nitpeek.client.console.Main"
}

repositories {
    maven {
        url = uri("https://plugins.gradle.org/m2/")
    }
}

// unfortunately automatic modules are contagious: we inherit them from lib-io
extraJavaModuleInfo {
    deriveAutomaticModuleNamesFromFileNames = true
}


tasks.startScripts {

    doLast {
        // Hack to add the plugins folder to the module path
        injectPluginsFolderToScript(windowsScript, ";%APP_HOME%\\\\plugins")
        injectPluginsFolderToScript(unixScript, ":\\\$APP_HOME/plugins")

        // Another hack: append log4j2 config location to DEFAULT_JVM_OPTS
        val log4jLocation = "log/log4j2.xml"
        val log4jOption = "-Dlog4j2.configurationFile"
        appendValueForParameter(windowsScript, "DEFAULT_JVM_OPTS", " $log4jOption=${pathRelativeToAppHomeWindows(log4jLocation)}")
        appendValueForQuotedParameter(unixScript, "DEFAULT_JVM_OPTS", " $log4jOption=${pathRelativeToAppHomeUnix(log4jLocation)}")

        // Another hack to work around docx4j not playing nice with java modules (defaultJvmOpts of startScripts seems broken on both platforms too)
        val openJaxbRuntimeToDocx4j = " --add-opens org.glassfish.jaxb.runtime/org.glassfish.jaxb.runtime.v2.runtime=org.docx4j.JAXB_ReferenceImpl"
        appendValueForParameter(windowsScript, "DEFAULT_JVM_OPTS", openJaxbRuntimeToDocx4j)
        appendValueForQuotedParameter(unixScript, "DEFAULT_JVM_OPTS", openJaxbRuntimeToDocx4j)
    }
}
private fun pathRelativeToAppHomeWindows(relativePath: String): String {
    return "%APP_HOME%\\\\${relativePath.split("/").joinToString(separator = "\\\\")}";
}

private fun pathRelativeToAppHomeUnix(relativePath: String): String {
    return "\\\$APP_HOME/$relativePath";
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