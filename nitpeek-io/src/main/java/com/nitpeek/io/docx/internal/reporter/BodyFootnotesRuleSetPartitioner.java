package com.nitpeek.io.docx.internal.reporter;

import com.nitpeek.core.api.process.RuleSetProvider;
import com.nitpeek.core.api.process.RuleSetTag;
import com.nitpeek.core.impl.process.RuleSetProviderFilters;

import java.util.Set;
import java.util.stream.Collectors;

import static com.nitpeek.core.api.process.StandardRuleSetTags.*;

public final class BodyFootnotesRuleSetPartitioner {

    private final Set<RuleSetProvider> universallyApplicableRules;
    private final Set<RuleSetProvider> rulesApplicableToBody;
    private final Set<RuleSetProvider> rulesApplicableToFootnotes;

    public BodyFootnotesRuleSetPartitioner(Set<RuleSetProvider> ruleSetProviders) {
        this.universallyApplicableRules = ruleSetProviders.stream()
                .filter(this::isRuleSetProviderUniversallyApplicable)
                .collect(Collectors.toUnmodifiableSet());
        this.rulesApplicableToBody = ruleSetProviders.stream()
                .filter(this::isRuleSetProviderApplicableToBody)
                .filter(rsp -> !universallyApplicableRules.contains(rsp))
                .collect(Collectors.toUnmodifiableSet());
        this.rulesApplicableToFootnotes = ruleSetProviders.stream()
                .filter(this::isRuleSetProviderApplicableToFootnotes)
                .filter(rsp -> !universallyApplicableRules.contains(rsp))
                .collect(Collectors.toUnmodifiableSet());
    }

    public Set<RuleSetProvider> rulesApplicableUniversally() {
        return universallyApplicableRules;
    }

    public Set<RuleSetProvider> rulesApplicableToBody() {
        return rulesApplicableToBody;
    }

    public Set<RuleSetProvider> rulesApplicableToFootnotes() {
        return rulesApplicableToFootnotes;
    }

    private boolean isRuleSetProviderUniversallyApplicable(RuleSetProvider ruleSetProvider) {
        if (RuleSetTag.keepTagsFromCategory(Category.CONTENT.getId(), ruleSetProvider.getTags()).isEmpty())
            return true; // by default, when no tags are configured, consider that the rule set provider is general purpose
        return RuleSetProviderFilters.matchesAllTags(ruleSetProvider, Set.of(contentAny()));
    }

    private boolean isRuleSetProviderApplicableToBody(RuleSetProvider ruleSetProvider) {
        return RuleSetProviderFilters.matchesAllTags(ruleSetProvider, Set.of(contentBody()));
    }

    private boolean isRuleSetProviderApplicableToFootnotes(RuleSetProvider ruleSetProvider) {
        return RuleSetProviderFilters.matchesAllTags(ruleSetProvider, Set.of(contentFootnotes()));
    }
}