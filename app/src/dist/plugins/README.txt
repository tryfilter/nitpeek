Place any plugins you wish to use in this directory.

Plugins are JAR files that provide at least one implementation of the com.nitpeek.core.api.plugin.Plugin service.

# JPMS-compatible specification
More specifically, a plugin JAR must contain a module-info.java with at a minimum the following declarations:

module com.example.demoplugin {
    requires com.nitpeek.lib.core;

    provides com.nitpeek.core.api.plugin.Plugin with com.example.demoplugin.plugin.DemoPlugin;
    opens com.example.demoplugin.plugin to com.nitpeek.lib.core;
}

The concrete module name and package names may differ but the module must declare to provide an implementation of the
com.nitpeek.core.api.plugin.Plugin interface and, crucially, it must open to the com.nitpeek.lib.core module the package which
contains said implementation.

# Legacy SPI support
To support contexts in which the module path is inaccessible or difficult to use, plugins should also define their
service implementation the "legacy" way: Add a file named "com.nitpeek.core.api.plugin.Plugin" with the contents being for
each implementation of the service the plugin provides one line containing the fully qualified name of the implementation
class at the required path (that is, under <PLUGIN_HOME>/src/main/resources/META-INF/services).
