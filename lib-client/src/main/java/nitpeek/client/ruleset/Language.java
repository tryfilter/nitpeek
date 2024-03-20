package nitpeek.client.ruleset;

import nitpeek.core.api.process.RuleSetTag;
import nitpeek.core.api.process.StandardRuleSetTags;

public enum Language {
    ENGLISH,
    GERMAN;

    public RuleSetTag tag() {
        return switch (this) {
            case ENGLISH -> StandardRuleSetTags.languageEnglish();
            case GERMAN -> StandardRuleSetTags.languageGerman();
        };
    }
}