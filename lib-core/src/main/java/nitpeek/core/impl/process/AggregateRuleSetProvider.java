package nitpeek.core.impl.process;

import nitpeek.core.api.analyze.Rule;
import nitpeek.core.api.common.Identifier;
import nitpeek.core.api.process.RuleSetProvider;
import nitpeek.core.impl.common.ProviderIdentifier;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import static nitpeek.core.impl.translate.CoreTranslationKeys.RULESET_AGGREGATE_ID_DESCRIPTION;
import static nitpeek.core.impl.translate.CoreTranslationKeys.RULESET_AGGREGATE_ID_NAME;

public final class AggregateRuleSetProvider implements RuleSetProvider {

    private final Set<RuleSetProvider> ruleSetProviders;

    public AggregateRuleSetProvider(Set<RuleSetProvider> ruleSetProviders) {
        this.ruleSetProviders = ruleSetProviders;
    }

    @Override
    public Identifier getRuleSetId() {
        return new ProviderIdentifier(
                () -> "nitpeek.ruleset.aggregate",
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
