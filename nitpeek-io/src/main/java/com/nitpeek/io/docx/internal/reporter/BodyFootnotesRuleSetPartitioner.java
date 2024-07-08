package com.nitpeek.io.docx.internal.reporter;

import com.nitpeek.core.api.analyze.Rule;
import com.nitpeek.core.api.process.RuleSetProvider;
import com.nitpeek.core.api.process.RuleSetTag;
import com.nitpeek.core.impl.process.FilteringRuleSetProvider;
import com.nitpeek.core.impl.process.RuleSetProviderFilters;
import com.nitpeek.core.impl.process.SimpleRuleSetProvider;

import java.util.HashSet;
import java.util.Set;

import static com.nitpeek.core.api.process.StandardRuleSetTags.*;

public final class BodyFootnotesRuleSetPartitioner {

    private final RuleSetProvider universallyApplicableRules;
    private final RuleSetProvider rulesApplicableToBody;
    private final RuleSetProvider rulesApplicableToFootnotes;

    public BodyFootnotesRuleSetPartitioner(Set<RuleSetProvider> ruleSetProviders) {
        this.universallyApplicableRules = new FilteringRuleSetProvider(ruleSetProviders, this::isRuleSetProviderUniversallyApplicable);
        this.rulesApplicableToBody = keepAllExcept(universallyApplicableRules.getRules(), new FilteringRuleSetProvider(ruleSetProviders, this::isRuleSetProviderApplicableToBody));
        this.rulesApplicableToFootnotes = keepAllExcept(universallyApplicableRules.getRules(), new FilteringRuleSetProvider(ruleSetProviders, this::isRuleSetProviderApplicableToFootnotes));
    }

    public Set<Rule> rulesApplicableUniversally() {
        return universallyApplicableRules.getRules();
    }

    public Set<Rule> rulesApplicableToBody() {
        return rulesApplicableToBody.getRules();
    }

    public Set<Rule> rulesApplicableToFootnotes() {
        return rulesApplicableToFootnotes.getRules();
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

    private RuleSetProvider keepAllExcept(Set<Rule> rulesToRemove, RuleSetProvider ruleSetProvider) {
        var resultingRules = new HashSet<>(ruleSetProvider.getRules());
        resultingRules.removeAll(rulesToRemove);
        return new SimpleRuleSetProvider(resultingRules, ruleSetProvider.getRuleSetId());
    }
}