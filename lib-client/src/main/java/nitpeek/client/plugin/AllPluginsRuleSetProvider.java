package nitpeek.client.plugin;

import nitpeek.core.api.analyze.Rule;
import nitpeek.core.api.common.Identifier;
import nitpeek.core.api.plugin.Plugin;
import nitpeek.core.api.process.RuleSetProvider;
import nitpeek.core.impl.common.SimpleIdentifier;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public final class AllPluginsRuleSetProvider implements RuleSetProvider {


    private final PluginManager pluginManager;

    public AllPluginsRuleSetProvider(PluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }

    @Override
    public Identifier getRuleSetId() {
        return new SimpleIdentifier("nitpeek.ALL_RULES_ACTIVE_PLUGINS",
                "nitpeek.client.ALL_RULES_ACTIVE_PLUGINS_NAME",
                "nitpeek.client.ALL_RULES_ACTIVE_PLUGINS_DESCRIPTION");
    }

    @Override
    public Set<Rule> getRules() {
        return combineAllAvailableRuleSets();
    }

    private Set<Rule> combineAllAvailableRuleSets() {
        return pluginManager.getPlugins().stream()
                .map(Plugin::getRuleSetProviders)
                .flatMap(Collection::stream)
                .map(RuleSetProvider::getRules)
                .flatMap(Collection::stream)
                .collect(Collectors.toUnmodifiableSet());
    }
}
