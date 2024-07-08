package com.nitpeek.core.impl.process;

import com.nitpeek.core.api.process.RuleSetProvider;
import com.nitpeek.core.api.process.RuleSetTag;

import java.util.Set;

public final class RuleSetProviderFilters {
    private RuleSetProviderFilters() {
    }

    public static boolean matchesAllTags(RuleSetProvider ruleSetProvider, Set<RuleSetTag> tags) {
        return ruleSetProvider.getTags().containsAll(tags);
    }

    public static boolean matchesAnyTags(RuleSetProvider ruleSetProvider, Set<RuleSetTag> tags) {
        return ruleSetProvider.getTags().stream().anyMatch(tags::contains);
    }
}