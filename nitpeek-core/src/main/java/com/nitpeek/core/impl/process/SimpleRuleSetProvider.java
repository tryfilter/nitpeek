package com.nitpeek.core.impl.process;

import com.nitpeek.core.api.analyze.Rule;
import com.nitpeek.core.api.common.FeatureFilter;
import com.nitpeek.core.api.common.Identifier;
import com.nitpeek.core.api.common.NoFilter;
import com.nitpeek.core.api.process.RuleSetProvider;
import com.nitpeek.core.api.process.RuleSetTag;

import java.util.Set;

public final class SimpleRuleSetProvider implements RuleSetProvider {

    private final Set<Rule> rules;
    private final Identifier identifier;

    private final Set<RuleSetTag> tags;

    private final FeatureFilter featureFilter;

    public SimpleRuleSetProvider(Set<Rule> rules, Identifier identifier) {
        this(rules, identifier, Set.of(), new NoFilter());
    }

    public SimpleRuleSetProvider(Set<Rule> rules, Identifier identifier, FeatureFilter featureFilter) {
        this(rules, identifier, Set.of(), featureFilter);
    }

    public SimpleRuleSetProvider(Set<Rule> rules, Identifier identifier, Set<RuleSetTag> tags) {
        this(rules, identifier, tags, new NoFilter());
    }

    public SimpleRuleSetProvider(Set<Rule> rules, Identifier identifier, Set<RuleSetTag> tags, FeatureFilter featureFilter) {
        this.rules = Set.copyOf(rules);
        this.identifier = identifier;
        this.tags = Set.copyOf(tags);
        this.featureFilter = featureFilter;
    }

    @Override
    public Set<Rule> getRules() {
        return rules;
    }

    @Override
    public Identifier getRuleSetId() {
        return identifier;
    }

    @Override
    public Set<RuleSetTag> getTags() {
        return tags;
    }

    @Override
    public FeatureFilter getFilter() {
        return featureFilter;
    }
}
