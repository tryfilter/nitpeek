package nitpeek.client.demo.rule;

import nitpeek.core.api.analyze.Rule;
import nitpeek.core.api.analyze.RuleType;
import nitpeek.core.api.translate.Translation;
import nitpeek.core.impl.analyze.SimpleRuleType;
import nitpeek.core.impl.analyze.analyzer.AggregatingAnalyzer;
import nitpeek.core.api.analyze.Analyzer;
import nitpeek.core.impl.analyze.analyzer.MissingPages;
import nitpeek.core.impl.analyze.analyzer.PageCounter;
import nitpeek.core.impl.translate.CoreTranslationKeys;
import nitpeek.core.impl.translate.SimpleDefaultEnglishTranslation;

import java.util.Set;
import java.util.function.Supplier;

public enum SampleRule implements Rule {

    DESCRIBE_PAGE_PROCESSING_INFORMATION(
            CoreTranslationKeys.DESCRIBE_PAGE_PROCESSING_INFO_RULE_NAME.key(),
            CoreTranslationKeys.DESCRIBE_PAGE_PROCESSING_INFO_RULE_DESCRIPTION.key(),
            () -> new AggregatingAnalyzer(Set.of(new PageCounter(), new MissingPages())));
    private final Translation defaultEnglishTranslation = new SimpleDefaultEnglishTranslation();
    private final String nameTranslationKey;
    private final String descriptionTranslationKey;
    private final Supplier<Analyzer> analyzer;

    SampleRule(String nameTranslationKey, String descriptionTranslationKey, Supplier<Analyzer> analyzer) {
        this.nameTranslationKey = nameTranslationKey;
        this.descriptionTranslationKey = descriptionTranslationKey;
        this.analyzer = analyzer;
    }

    /**
     * @return the RuleType of this sample rule, with its nameTranslationKey and descriptionTranslationKey translated by the standard english translator
     */
    public RuleType getType() {
        return getType(defaultEnglishTranslation);
    }

    @Override
    public Analyzer createAnalyzer() {
        return analyzer.get();
    }

    /**
     * @param i18n the translator to use as a source for the rule nameTranslationKey and rule descriptionTranslationKey
     * @return the SimpleRuleType of this sample rule, with its nameTranslationKey and descriptionTranslationKey translated by the provided translator
     */
    public SimpleRuleType getType(Translation i18n) {
        return new SimpleRuleType("nitpeek.demo.rule." + this.name(), i18n.translate(nameTranslationKey), i18n.translate(descriptionTranslationKey));
    }
}
