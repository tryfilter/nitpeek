package nitpeek.client.demo.rule;

import nitpeek.core.api.analyze.Rule;
import nitpeek.core.api.analyze.RuleType;
import nitpeek.core.api.analyze.analyzer.AggregatingAnalyzer;
import nitpeek.core.api.analyze.analyzer.Analyzer;
import nitpeek.core.api.analyze.analyzer.MissingPages;
import nitpeek.core.api.analyze.analyzer.PageCounter;
import nitpeek.translation.DefaultEnglishTranslator;
import nitpeek.translation.Translator;

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
     * @return the RuleType of this sample rule, with its name and description translated by the standard english translator
     */
    public RuleType getType() {
        return getType(defaultEnglishTranslator);
    }

    @Override
    public Analyzer getAnalyzer() {
        return analyzer;
    }

    /**
     * @param translator the translator to use as a source for the rule name and rule description
     * @return the RuleType of this sample rule, with its name and description translated by the provided translator
     */
    public RuleType getType(Translator translator) {
        return new RuleType("nitpeek.demo.rule." + this.name(), nameSupplier.apply(translator), descriptionSupplier.apply(translator));
    }
}
