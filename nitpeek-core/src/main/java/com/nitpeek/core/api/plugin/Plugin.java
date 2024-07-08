package com.nitpeek.core.api.plugin;

import com.nitpeek.core.api.common.Identifier;
import com.nitpeek.core.api.process.RuleSetProvider;
import com.nitpeek.core.api.translate.TranslationProvider;
import com.nitpeek.core.impl.common.ProviderIdentifier;

import java.util.Set;

import static com.nitpeek.core.impl.translate.CoreTranslationKeys.PLUGIN_ALL_RULES_ID_DESCRIPTION;
import static com.nitpeek.core.impl.translate.CoreTranslationKeys.PLUGIN_ALL_RULES_ID_NAME;

public interface Plugin {

    Identifier getPluginId();

    TranslationProvider getTranslationProvider();

    RuleSetProvider getAllRules();
    Set<RuleSetProvider> getRuleSetProviders();


    default Identifier getAllRulesId() {
        return new ProviderIdentifier(
                () -> allRulesId(getPluginId().getId()),
                i18n -> i18n.translate(PLUGIN_ALL_RULES_ID_NAME.key(), getAllRules().getRuleSetId().getName(i18n)),
                i18n -> i18n.translate(PLUGIN_ALL_RULES_ID_DESCRIPTION.key(), getAllRules().getRuleSetId().getDescription(i18n))
        );
    }

    static String allRulesId(String baseId) {
        return baseId + ".EXPORTED_RULES";
    }
}
