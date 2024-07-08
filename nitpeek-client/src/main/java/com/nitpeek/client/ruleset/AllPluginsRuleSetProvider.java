package com.nitpeek.client.ruleset;

import com.nitpeek.client.plugin.PluginManager;
import com.nitpeek.core.api.analyze.Rule;
import com.nitpeek.core.api.common.Identifier;
import com.nitpeek.core.api.plugin.Plugin;
import com.nitpeek.core.api.process.RuleSetProvider;
import com.nitpeek.core.impl.process.FilteringRuleSetProvider;

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
