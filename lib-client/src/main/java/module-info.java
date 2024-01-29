module nitpeek.lib.client {
    requires nitpeek.lib.core;
    exports nitpeek.client.plugin;
    exports nitpeek.client.application;
    exports nitpeek.client.translation;

    uses nitpeek.core.api.plugin.Plugin;
    opens nitpeek.client.application.internal to nitpeek.lib.core;
}