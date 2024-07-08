module com.nitpeek.lib.client {
    requires com.nitpeek.lib.core;
    requires org.slf4j;

    exports com.nitpeek.client.plugin;
    exports com.nitpeek.client.application;
    exports com.nitpeek.client.translation;
    exports com.nitpeek.client.ruleset;

    uses com.nitpeek.core.api.plugin.Plugin;
    opens com.nitpeek.client.application.internal to com.nitpeek.lib.core;
}