module nitpeek.lib.client {
    requires nitpeek.lib.core;
    exports nitpeek.client.plugin;

    uses nitpeek.core.api.plugin.Plugin;
}