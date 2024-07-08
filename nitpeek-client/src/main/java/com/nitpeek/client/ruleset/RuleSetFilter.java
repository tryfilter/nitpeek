package com.nitpeek.client.ruleset;

import com.nitpeek.core.api.process.RuleSetProvider;

import java.util.Set;

public interface RuleSetFilter {
    Set<RuleSetProvider> filter(Set<RuleSetProvider> ruleSetProviders);
}