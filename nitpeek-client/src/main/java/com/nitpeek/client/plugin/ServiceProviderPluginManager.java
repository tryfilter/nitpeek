package com.nitpeek.client.plugin;

import com.nitpeek.core.api.plugin.Plugin;

import java.util.ServiceLoader;
import java.util.Set;

public final class ServiceProviderPluginManager implements PluginManager {
    @Override
    public Set<Plugin> getPlugins() {
        ServiceLoader<Plugin> plugins = ServiceLoader.load(Plugin.class);
        return Set.copyOf(plugins.stream().map(ServiceLoader.Provider::get).toList());
    }
}
