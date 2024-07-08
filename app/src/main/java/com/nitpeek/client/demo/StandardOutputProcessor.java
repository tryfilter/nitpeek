package com.nitpeek.client.demo;

import com.nitpeek.core.api.analyze.AnalyzerOfRule;
import com.nitpeek.core.api.common.TextPage;
import com.nitpeek.core.api.process.*;
import com.nitpeek.core.api.report.FeatureFormatter;
import com.nitpeek.core.api.report.Reporter;
import com.nitpeek.core.api.report.ReportingException;
import com.nitpeek.core.api.report.ReportingTarget;
import com.nitpeek.core.api.translate.Translation;
import com.nitpeek.core.impl.report.IndentingFeatureFormatter;
import com.nitpeek.core.impl.report.SeparatorReporter;
import com.nitpeek.core.impl.report.WriterReportingTarget;
import com.nitpeek.core.impl.translate.DefaultFallbackEnglishTranslation;

import java.io.PrintWriter;
import java.util.Set;
import java.util.stream.Collectors;

import static com.nitpeek.core.impl.translate.CoreTranslationKeys.APPLIED_RULE_DESCRIPTION;
import static com.nitpeek.core.impl.translate.CoreTranslationKeys.APPLIED_RULE_NAME;

public final class StandardOutputProcessor implements Processor {

    private final ReportingTarget reportingTarget = new WriterReportingTarget(new PrintWriter(System.out));
    private final RuleSetProvider ruleSetProvider;
    private final Reporter featureReporter;
    private final Translation i18n;

    public StandardOutputProcessor(RuleSetProvider ruleSetProvider) {
        this(ruleSetProvider, new DefaultFallbackEnglishTranslation());
    }

    public StandardOutputProcessor(RuleSetProvider ruleSetProvider, Translation i18n) {
        this(ruleSetProvider, i18n, new IndentingFeatureFormatter(i18n));
    }

    public StandardOutputProcessor(RuleSetProvider ruleSetProvider, Translation i18n, FeatureFormatter featureFormatter) {
        this.ruleSetProvider = ruleSetProvider;
        this.featureReporter = new SeparatorReporter("\n", reportingTarget, featureFormatter);
        this.i18n = i18n;
    }

    @Override
    public void startProcessing(PageSource pageSource) {
        var innerConsumer = new Consumer();
        pageSource.dischargeTo(innerConsumer);
    }

    // Delegate to this private inner class, so we don't need to expose the PageConsumer API to our clients.
    private final class Consumer implements PageConsumer<Void> {
        private final Set<AnalyzerOfRule> analyzers = ruleSetProvider.getRules().stream()
                .map(AnalyzerOfRule::createFrom)
                .collect(Collectors.toUnmodifiableSet());

        @Override
        public void consumePage(TextPage page) {
            for (var analyzer : analyzers) {
                analyzer.analyzer().processPage(page);
            }
        }

        @Override
        public Void finish() {
            try {
                for (var analyzerOfRule : analyzers) {
                    reportRule(analyzerOfRule);
                }
            } catch (ReportingException e) {
                throw new IllegalStateException("Exception when trying to write to std out. Not much we can do at this point.", e);
            }
            return null;
        }

        private void reportRule(AnalyzerOfRule rule) throws ReportingException {
            var type = rule.ruleType();
            reportingTarget.report(i18n.translate(APPLIED_RULE_NAME.key(), type.getRuleId().getName(i18n)));
            reportingTarget.report("\n");
            reportingTarget.report(i18n.translate(APPLIED_RULE_DESCRIPTION.key(), type.getRuleId().getDescription(i18n)));
            reportingTarget.report("\n");
            featureReporter.reportFeatures(rule.analyzer().findFeatures(), i18n);
            reportingTarget.report("\n");
            reportingTarget.report("\n");
            reportingTarget.report("\n");
        }
    }
}