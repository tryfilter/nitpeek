package nitpeek.client.ruleset;

import nitpeek.core.api.process.RuleSetProvider;

import java.util.Set;

public interface RuleSetFilter {
    Set<RuleSetProvider> filter(Set<RuleSetProvider> ruleSetProviders);
}