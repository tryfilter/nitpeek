package com.nitpeek.client.plugin;

import com.nitpeek.core.api.plugin.Plugin;
import com.nitpeek.core.api.process.RuleSetProvider;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public interface PluginManager {

    Set<Plugin> getPlugins();
    default Set<RuleSetProvider> getRuleSetProviders() {
        return getPlugins().stream().map(Plugin::getRuleSetProviders).flatMap(Collection::stream).collect(Collectors.toUnmodifiableSet());
    }
}
