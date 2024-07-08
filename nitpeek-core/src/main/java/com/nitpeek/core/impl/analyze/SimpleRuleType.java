package com.nitpeek.core.impl.analyze;

import com.nitpeek.core.api.analyze.RuleType;
import com.nitpeek.core.api.common.Identifier;
import com.nitpeek.core.impl.common.SimpleIdentifier;

public record SimpleRuleType(String id, String nameTranslationKey, String descriptionTranslationKey) implements RuleType {
    @Override
    public Identifier getRuleId() {
        return new SimpleIdentifier(id, nameTranslationKey, descriptionTranslationKey);
    }
}
