package com.nitpeek.core.impl.process;

import com.nitpeek.core.api.analyze.Rule;
import com.nitpeek.core.api.common.Identifier;
import com.nitpeek.core.api.process.RuleSetProvider;
import com.nitpeek.core.impl.common.ProviderIdentifier;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import static com.nitpeek.core.impl.translate.CoreTranslationKeys.RULESET_AGGREGATE_ID_DESCRIPTION;
import static com.nitpeek.core.impl.translate.CoreTranslationKeys.RULESET_AGGREGATE_ID_NAME;

public final class AggregateRuleSetProvider implements RuleSetProvider {

    private final Set<RuleSetProvider> ruleSetProviders;

    public AggregateRuleSetProvider(Set<RuleSetProvider> ruleSetProviders) {
        this.ruleSetProviders = ruleSetProviders;
    }

    @Override
    public Identifier getRuleSetId() {
        return new ProviderIdentifier(
                () -> "com.nitpeek.ruleset.aggregate",
                i18n -> i18n.translate(RULESET_AGGREGATE_ID_NAME.key()),
                i18n -> i18n.translate(RULESET_AGGREGATE_ID_DESCRIPTION.key(), concatenateRuleSetIds())
        );
    }

    private String concatenateRuleSetIds() {
        var ruleSetIds = ruleSetProviders
                .stream()
                .map(RuleSetProvider::getRuleSetId)
                .map(Identifier::getId)
                .toList();
        return String.join(", ", ruleSetIds);
    }

    @Override
    public Set<Rule> getRules() {
        return ruleSetProviders
                .stream()
                .map(RuleSetProvider::getRules)
                .flatMap(Collection::stream)
                .collect(Collectors.toUnmodifiableSet());
    }
}
