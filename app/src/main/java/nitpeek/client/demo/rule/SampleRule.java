package nitpeek.client.demo.rule;

import nitpeek.core.api.analyze.Rule;
import nitpeek.core.impl.analyze.SimpleRuleType;
import nitpeek.core.impl.analyze.analyzer.AggregatingAnalyzer;
import nitpeek.core.api.analyze.Analyzer;
import nitpeek.core.impl.analyze.analyzer.MissingPages;
import nitpeek.core.impl.analyze.analyzer.PageCounter;
import nitpeek.core.impl.translate.helper.DefaultEnglishTranslator;
import nitpeek.core.impl.translate.helper.Translator;

import java.util.Set;
import java.util.function.Function;

public enum SampleRule implements Rule {

    DESCRIBE_PAGE_PROCESSING_INFORMATION(
            Translator::describePageProcessingInfoRuleName,
            Translator::describePageProcessingInfoRuleDescription,
            new AggregatingAnalyzer(Set.of(new PageCounter(), new MissingPages())));
    private final Translator defaultEnglishTranslator = new DefaultEnglishTranslator();
    private final Function<Translator, String> nameSupplier;
    private final Function<Translator, String> descriptionSupplier;
    private final Analyzer analyzer;

    SampleRule(Function<Translator, String> nameSupplier, Function<Translator, String> descriptionSupplier, Analyzer analyzer) {
        this.nameSupplier = nameSupplier;
        this.descriptionSupplier = descriptionSupplier;
        this.analyzer = analyzer;
    }

    /**
     * @return the SimpleRuleType of this sample rule, with its nameTranslationKey and descriptionTranslationKey translated by the standard english translator
     */
    public SimpleRuleType getType() {
        return getType(defaultEnglishTranslator);
    }

    @Override
    public Analyzer getAnalyzer() {
        return analyzer;
    }

    /**
     * @param i18n the translator to use as a source for the rule nameTranslationKey and rule descriptionTranslationKey
     * @return the SimpleRuleType of this sample rule, with its nameTranslationKey and descriptionTranslationKey translated by the provided translator
     */
    public SimpleRuleType getType(Translator i18n) {
        return new SimpleRuleType("nitpeek.demo.rule." + this.name(), nameSupplier.apply(i18n), descriptionSupplier.apply(i18n));
    }
}
