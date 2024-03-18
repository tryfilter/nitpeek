package nitpeek.io.docx.internal.reporter;

import nitpeek.core.api.analyze.Rule;
import nitpeek.core.api.process.RuleSetProvider;
import nitpeek.core.api.process.StandardRuleSetTags;
import nitpeek.core.impl.process.FilteringRuleSetProvider;
import nitpeek.core.impl.process.RuleSetProviderFilters;
import nitpeek.core.impl.process.SimpleRuleSetProvider;

import java.util.HashSet;
import java.util.Set;

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
        if (ruleSetProvider.getTags().isEmpty())
            return true; // by default, when no tags are configured, consider that the rule set provider is general purpose
        return RuleSetProviderFilters.matchesAllTags(ruleSetProvider, Set.of(StandardRuleSetTags.contentAny()));
    }

    private boolean isRuleSetProviderApplicableToBody(RuleSetProvider ruleSetProvider) {
        return RuleSetProviderFilters.matchesAllTags(ruleSetProvider, Set.of(StandardRuleSetTags.contentBody()));
    }

    private boolean isRuleSetProviderApplicableToFootnotes(RuleSetProvider ruleSetProvider) {
        return RuleSetProviderFilters.matchesAllTags(ruleSetProvider, Set.of(StandardRuleSetTags.contentFootnotes()));
    }

    private RuleSetProvider keepAllExcept(Set<Rule> rulesToRemove, RuleSetProvider ruleSetProvider) {
        var resultingRules = new HashSet<>(ruleSetProvider.getRules());
        resultingRules.removeAll(rulesToRemove);
        return new SimpleRuleSetProvider(resultingRules, ruleSetProvider.getRuleSetId());
    }
}