Place any plugins you wish to use in this directory.

Plugins are JAR files that contain a module which provides an implementation of the nitpeek.core.api.plugin.Plugin service.

More specifically, a plugin JAR must contain a module-info.java with at a minimum the following declarations:

module com.example.demoplugin {
    requires nitpeek.lib.core;

    provides nitpeek.core.api.plugin.Plugin with com.example.demoplugin.plugin.DemoPlugin;
    opens com.example.demoplugin.plugin to nitpeek.lib.core;
}

The concrete module name and package name may differ but the module must declare to provide an implementation of the
nitpeek.core.api.plugin.Plugin interface and, crucially, it must open to the nitpeek.lib.core module the package which
contains said implementation.

