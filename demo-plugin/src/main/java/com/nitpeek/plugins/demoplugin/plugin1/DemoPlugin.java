package com.nitpeek.plugins.demoplugin.plugin1;

import com.nitpeek.plugins.demoplugin.plugin1.internal.DemoTranslationProvider;
import com.nitpeek.plugins.demoplugin.plugin1.internal.InternalTranslationKeys;
import com.nitpeek.core.api.analyze.Rule;
import com.nitpeek.core.api.common.Identifier;
import com.nitpeek.core.api.plugin.Plugin;
import com.nitpeek.core.api.process.RuleSetProvider;
import com.nitpeek.core.api.translate.TranslationProvider;
import com.nitpeek.core.impl.analyze.SimpleRule;
import com.nitpeek.core.impl.analyze.SimpleRuleType;
import com.nitpeek.core.impl.analyze.analyzer.LiteralReplacer;
import com.nitpeek.core.impl.analyze.analyzer.RegexReplacer;
import com.nitpeek.core.impl.common.SimpleIdentifier;
import com.nitpeek.core.impl.process.SimpleRuleSetProvider;

import java.util.Set;
import java.util.regex.Pattern;

public final class DemoPlugin implements Plugin {

    public static final String ID = "com.nitpeek.demo.plugin1";

    private static String id(String id) {
        return ID + "." + id;
    }

    private static final Rule replace42WithMeaning = new SimpleRule(
            new SimpleRuleType(id("REPLACE_42"), InternalTranslationKeys.REPLACE_42_NAME.key(), InternalTranslationKeys.REPLACE_42_DESCRIPTION.key()),
            () -> new LiteralReplacer("42", "Meaning Of Life")
    );

    private static final Rule replace4xWith42 = new SimpleRule(
            new SimpleRuleType(id("REPLACE_4X"), InternalTranslationKeys.REPLACE_4X_NAME.key(), InternalTranslationKeys.REPLACE_4X_DESCRIPTION.key()),
            () -> new RegexReplacer(Pattern.compile("4[013-9]"), "42")
    );

    private static final Rule deleteTest = new SimpleRule(
            new SimpleRuleType(id("DELETE_TEST"), InternalTranslationKeys.DELETE_TEST_NAME.key(), InternalTranslationKeys.DELETE_TEST_DESCRIPTION.key()),
            () -> new LiteralReplacer("test", "", true)
    );
    @Override
    public Identifier getPluginId() {
        return new SimpleIdentifier(ID, InternalTranslationKeys.PLUGIN1_NAME.key(), InternalTranslationKeys.PLUGIN1_DESCRIPTION.key());
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
                        new SimpleIdentifier(id("MEANING_OF_LIFE"), InternalTranslationKeys.RULESET_MEANING_NAME.key(), InternalTranslationKeys.RULESET_MEANING_DESCRIPTION.key())
                ),
                new SimpleRuleSetProvider(
                        Set.of(deleteTest),
                        new SimpleIdentifier(id("DELETION"), InternalTranslationKeys.RULESET_DELETE_NAME.key(), InternalTranslationKeys.RULESET_DELETE_DESCRIPTION.key())
                )
        );
    }
}
