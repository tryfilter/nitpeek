package nitpeek.core.api.plugin;

import nitpeek.core.api.common.Identifier;
import nitpeek.core.api.process.RuleSetProvider;
import nitpeek.core.api.translate.TranslationProvider;
import nitpeek.core.impl.common.ProviderIdentifier;

import java.util.Set;

import static nitpeek.core.impl.translate.CoreTranslationKeys.PLUGIN_ALL_RULES_ID_DESCRIPTION;
import static nitpeek.core.impl.translate.CoreTranslationKeys.PLUGIN_ALL_RULES_ID_NAME;

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
