package nitpeek.core.api.process;

import nitpeek.core.api.analyze.Rule;

import java.util.Set;

public final class SimpleRuleSetProvider implements RuleSetProvider{

    private final Set<Rule> rules;

    public SimpleRuleSetProvider(Set<Rule> rules) {
        this.rules = Set.copyOf(rules);
    }

    /**
     * @return an unmodifiable copy
     */
    @Override
    public Set<Rule> getRules() {
        return rules;
    }
}
