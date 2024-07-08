package com.nitpeek.core.impl.process;

import com.nitpeek.core.api.analyze.Rule;
import com.nitpeek.core.api.common.Identifier;
import com.nitpeek.core.api.process.RuleSetProvider;
import com.nitpeek.core.api.process.RuleSetTag;

import java.util.Set;

public final class SimpleRuleSetProvider implements RuleSetProvider {

    private final Set<Rule> rules;
    private final Identifier identifier;

    private final Set<RuleSetTag> tags;

    public SimpleRuleSetProvider(Set<Rule> rules, Identifier identifier) {
        this(rules, identifier, Set.of());
    }

    public SimpleRuleSetProvider(Set<Rule> rules, Identifier identifier, Set<RuleSetTag> tags) {
        this.rules = Set.copyOf(rules);
        this.identifier = identifier;
        this.tags = Set.copyOf(tags);
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
}
