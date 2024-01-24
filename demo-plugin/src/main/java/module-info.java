import nitpeek.core.api.plugin.Plugin;
import nitpeek.plugins.demoplugin.plugin1.DemoPlugin;

module nitpeek.demo.plugins.demoplugin {
    requires nitpeek.lib.core;
    
    provides Plugin with DemoPlugin;
    opens nitpeek.plugins.demoplugin.plugin1 to nitpeek.lib.core;
}