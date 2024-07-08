import com.nitpeek.core.api.plugin.Plugin;
import com.nitpeek.plugins.demoplugin.plugin1.DemoPlugin;

module com.nitpeek.demo.plugins.demoplugin {
    requires com.nitpeek.lib.core;
    
    provides Plugin with DemoPlugin;
    opens com.nitpeek.plugins.demoplugin.plugin1 to com.nitpeek.lib.core;
}