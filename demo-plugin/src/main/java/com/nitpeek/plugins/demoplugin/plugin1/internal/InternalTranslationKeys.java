package com.nitpeek.plugins.demoplugin.plugin1.internal;

import com.nitpeek.plugins.demoplugin.plugin1.DemoPlugin;

public enum InternalTranslationKeys {
    PLUGIN1_NAME,
    PLUGIN1_DESCRIPTION,
    RULESET_MEANING_NAME,
    RULESET_MEANING_DESCRIPTION,

    RULESET_DELETE_NAME,
    RULESET_DELETE_DESCRIPTION,
    REPLACE_42_NAME,
    REPLACE_42_DESCRIPTION,
    REPLACE_4X_NAME,
    REPLACE_4X_DESCRIPTION,
    DELETE_TEST_NAME,
    DELETE_TEST_DESCRIPTION,
    ;

    public String key() {
        return DemoPlugin.ID + "." + name();
    }
}
