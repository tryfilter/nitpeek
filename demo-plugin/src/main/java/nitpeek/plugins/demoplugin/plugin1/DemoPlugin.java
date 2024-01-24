package nitpeek.plugins.demoplugin.plugin1;

import nitpeek.core.api.analyze.Rule;
import nitpeek.core.api.common.Identifier;
import nitpeek.core.api.plugin.Plugin;
import nitpeek.core.api.process.RuleSetProvider;
import nitpeek.core.api.translate.TranslationProvider;
import nitpeek.core.impl.analyze.SimpleRule;
import nitpeek.core.impl.analyze.SimpleRuleType;
import nitpeek.core.impl.analyze.analyzer.LiteralReplacer;
import nitpeek.core.impl.analyze.analyzer.RegexReplacer;
import nitpeek.core.impl.common.SimpleIdentifier;
import nitpeek.core.impl.process.SimpleRuleSetProvider;
import nitpeek.plugins.demoplugin.plugin1.internal.DemoTranslationProvider;

import java.util.Set;
import java.util.regex.Pattern;

import static nitpeek.plugins.demoplugin.plugin1.internal.InternalTranslationKeys.*;

public final class DemoPlugin implements Plugin {

    public static final String ID = "nitpeek.demo.plugin1";

    private static String id(String id) {
        return ID + "." + id;
    }

    private static final Rule replace42WithMeaning = new SimpleRule(
            new SimpleRuleType(id("REPLACE_42"), REPLACE_42_NAME.key(), REPLACE_42_DESCRIPTION.key()),
            () -> new LiteralReplacer("42", "Meaning Of Life")
    );

    private static final Rule replace4xWith42 = new SimpleRule(
            new SimpleRuleType(id("REPLACE_4X"), REPLACE_4X_NAME.key(), REPLACE_4X_DESCRIPTION.key()),
            () -> new RegexReplacer(Pattern.compile("4[013-9]"), "42")
    );

    private static final Rule deleteTest = new SimpleRule(
            new SimpleRuleType(id("DELETE_TEST"), DELETE_TEST_NAME.key(), DELETE_TEST_DESCRIPTION.key()),
            () -> new LiteralReplacer("test", "", true)
    );
    @Override
    public Identifier getPluginId() {
        return new SimpleIdentifier(ID, PLUGIN1_NAME.key(), PLUGIN1_DESCRIPTION.key());
    }

    @Override
    public TranslationProvider getTranslationProvider() {
        return new DemoTranslationProvider();
    }

    @Override
    public RuleSetProvider getAllRules() {
        return new SimpleRuleSetProvider(
                getAllDefinedRules(),
                getAllRulesId()
        );
    }

    private Set<Rule> getAllDefinedRules() {

        return Set.of(replace42WithMeaning, replace4xWith42, deleteTest);
    }

    @Override
    public Set<RuleSetProvider> getRuleSetProviders() {
        return Set.of(
                new SimpleRuleSetProvider(
                        Set.of(replace42WithMeaning, replace4xWith42),
                        new SimpleIdentifier(id("MEANING_OF_LIFE"), RULESET_MEANING_NAME.key(), RULESET_MEANING_DESCRIPTION.key())
                ),
                new SimpleRuleSetProvider(
                        Set.of(deleteTest),
                        new SimpleIdentifier(id("DELETION"), RULESET_DELETE_NAME.key(), RULESET_DELETE_DESCRIPTION.key())
                )
        );
    }
}
