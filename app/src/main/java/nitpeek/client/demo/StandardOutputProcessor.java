package nitpeek.client.demo;

import nitpeek.core.api.analyze.Rule;
import nitpeek.core.api.analyze.TextPage;
import nitpeek.core.api.process.PageConsumer;
import nitpeek.core.api.process.PageSource;
import nitpeek.core.api.process.Processor;
import nitpeek.core.api.process.RuleSetProvider;
import nitpeek.core.api.report.*;
import nitpeek.translation.DefaultEnglishTranslator;
import nitpeek.translation.Translator;

import java.io.PrintWriter;

public final class StandardOutputProcessor implements Processor, PageConsumer {

    private final ReportingTarget reportingTarget = new WriterReportingTarget(new PrintWriter(System.out));
    private final RuleSetProvider ruleSetProvider;
    private final Reporter featureReporter;
    private final Translator translator;

    public StandardOutputProcessor(RuleSetProvider ruleSetProvider) {
        this(ruleSetProvider, new DefaultEnglishTranslator());
    }
    public StandardOutputProcessor(RuleSetProvider ruleSetProvider, Translator translator) {
        this(ruleSetProvider, translator, new IndentingFeatureFormatter(translator));
    }

    public StandardOutputProcessor(RuleSetProvider ruleSetProvider, Translator translator, FeatureFormatter featureFormatter) {
        this.ruleSetProvider = ruleSetProvider;
        this.featureReporter = new SeparatorReporter("\n", reportingTarget, featureFormatter);
        this.translator = translator;
    }

    @Override
    public void startProcessing(PageSource pageSource) {
        pageSource.dischargeTo(this);
    }

    @Override
    public void consumePage(TextPage page) {
        for (var rule : ruleSetProvider.getRules()) {
            rule.getAnalyzer().processPage(page);
        }
    }

    @Override
    public void finish() {
        try {
            for (var rule : ruleSetProvider.getRules()) {
                reportRule(rule);
            }
        } catch (ReportingException e) {
            throw new IllegalStateException("Exception when trying to write to std out. Not much we can do at this point.", e);
        }

    }

    private void reportRule(Rule rule) throws ReportingException {
        var type = rule.getType();
        reportingTarget.report(translator.appliedRuleName(type.name()));
        reportingTarget.report("\n");
        reportingTarget.report(translator.appliedRuleDescription(type.description()));
        reportingTarget.report("\n");
        featureReporter.reportFeatures(rule.getAnalyzer().findFeatures());
        reportingTarget.report("\n");
        reportingTarget.report("\n");
        reportingTarget.report("\n");
    }
}
