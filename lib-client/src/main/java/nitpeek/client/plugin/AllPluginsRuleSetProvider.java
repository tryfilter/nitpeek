package nitpeek.client.plugin;

import nitpeek.core.api.analyze.Rule;
import nitpeek.core.api.common.Identifier;
import nitpeek.core.api.plugin.Plugin;
import nitpeek.core.api.process.RuleSetProvider;

import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class AllPluginsRuleSetProvider implements RuleSetProvider {

    private final PluginManager pluginManager;
    private final Predicate<RuleSetProvider> ruleSetProviderFilter;

    public AllPluginsRuleSetProvider(PluginManager pluginManager) {
        this(pluginManager, ruleSetProvider -> true);
    }

    public AllPluginsRuleSetProvider(PluginManager pluginManager, Predicate<RuleSetProvider> ruleSetProviderFilter) {
        this.pluginManager = pluginManager;
        this.ruleSetProviderFilter = ruleSetProviderFilter;
    }

    @Override
    public Identifier getRuleSetId() {
        return getAggregateRuleSetProvider().getRuleSetId();
    }

    @Override
    public Set<Rule> getRules() {
        return combineAllAvailableRuleSets();
    }

    private Set<Rule> combineAllAvailableRuleSets() {
        return getAggregateRuleSetProvider().getRules();
    }

    private RuleSetProvider getAggregateRuleSetProvider() {
        var currentlyAvailableRuleSets = pluginManager.getPlugins()
                .stream()
                .map(Plugin::getRuleSetProviders)
                .flatMap(Collection::stream)
                .collect(Collectors.toUnmodifiableSet());
        return new FilteringRuleSetProvider(currentlyAvailableRuleSets, ruleSetProviderFilter);
    }
}
