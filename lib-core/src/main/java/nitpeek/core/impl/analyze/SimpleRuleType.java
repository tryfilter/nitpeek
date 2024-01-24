package nitpeek.core.impl.analyze;

import nitpeek.core.api.common.Identifier;
import nitpeek.core.api.analyze.RuleType;
import nitpeek.core.impl.common.SimpleIdentifier;

public record SimpleRuleType(String id, String nameTranslationKey, String descriptionTranslationKey) implements RuleType {
    @Override
    public Identifier getRuleId() {
        return new SimpleIdentifier(id, nameTranslationKey, descriptionTranslationKey);
    }
}
