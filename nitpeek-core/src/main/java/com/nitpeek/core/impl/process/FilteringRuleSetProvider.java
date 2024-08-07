package com.nitpeek.core.impl.process;

import com.nitpeek.core.api.analyze.Rule;
import com.nitpeek.core.api.common.Identifier;
import com.nitpeek.core.api.process.RuleSetProvider;

import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class FilteringRuleSetProvider implements RuleSetProvider {
    private final RuleSetProvider aggregateRuleSetProvider;

    public FilteringRuleSetProvider(Set<RuleSetProvider> ruleSetProviders, Predicate<RuleSetProvider> ruleSetProviderFilter) {
        var filteredProviders = ruleSetProviders.stream().filter(ruleSetProviderFilter).collect(Collectors.toUnmodifiableSet());
        this.aggregateRuleSetProvider = new AggregateRuleSetProvider(filteredProviders);
    }

    @Override
    public Identifier getRuleSetId() {
        return aggregateRuleSetProvider.getRuleSetId();
    }

    @Override
    public Set<Rule> getRules() {
        return aggregateRuleSetProvider.getRules();
    }
}
