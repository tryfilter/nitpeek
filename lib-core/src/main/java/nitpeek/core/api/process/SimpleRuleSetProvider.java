package nitpeek.core.api.process;

import nitpeek.core.api.analyze.Rule;
import nitpeek.core.api.common.Identifier;

import java.util.Set;

public final class SimpleRuleSetProvider implements RuleSetProvider{

    private final Set<Rule> rules;
    private final Identifier identifier;

    public SimpleRuleSetProvider(Set<Rule> rules, Identifier identifier) {
        this.rules = Set.copyOf(rules);
        this.identifier = identifier;
    }

    /**
     * @return an unmodifiable copy
     */
    @Override
    public Set<Rule> getRules() {
        return rules;
    }

    @Override
    public Identifier getRuleSetId() {
        return identifier;
    }
}
