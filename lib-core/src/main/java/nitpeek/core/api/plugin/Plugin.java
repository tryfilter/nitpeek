package nitpeek.core.api.plugin;

import nitpeek.core.api.common.Identifier;
import nitpeek.core.api.process.RuleSetProvider;
import nitpeek.core.api.translate.TranslationProvider;

import java.util.Set;

public interface Plugin {

    Identifier getPluginId();

    TranslationProvider getTranslationProvider();

    RuleSetProvider getAllRules();
    Set<RuleSetProvider> getRuleSetProviders();
}
