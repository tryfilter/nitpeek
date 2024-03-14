package nitpeek.client.plugin;

import nitpeek.core.api.process.RuleSetProvider;
import nitpeek.core.api.process.RuleSetTag;

import java.util.Set;

public final class RuleSetProviderFilters {
    public static boolean matchesAllTags(RuleSetProvider ruleSetProvider, Set<RuleSetTag> tags) {
        return ruleSetProvider.getTags().containsAll(tags);
    }

    public static boolean matchesAnyTags(RuleSetProvider ruleSetProvider, Set<RuleSetTag> tags) {
        return ruleSetProvider.getTags().stream().anyMatch(tags::contains);
    }
}