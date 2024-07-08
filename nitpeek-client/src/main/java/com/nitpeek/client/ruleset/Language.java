package com.nitpeek.client.ruleset;

import com.nitpeek.core.api.process.RuleSetTag;
import com.nitpeek.core.api.process.StandardRuleSetTags;

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