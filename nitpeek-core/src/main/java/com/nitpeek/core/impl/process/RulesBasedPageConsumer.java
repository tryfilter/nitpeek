package com.nitpeek.core.impl.process;

import com.nitpeek.core.api.analyze.AnalyzerOfRule;
import com.nitpeek.core.api.analyze.Rule;
import com.nitpeek.core.api.common.Feature;
import com.nitpeek.core.api.common.TextPage;
import com.nitpeek.core.api.process.FeatureProducingPageConsumer;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class RulesBasedPageConsumer implements FeatureProducingPageConsumer {
    private boolean isFinished = false;

    private final Set<AnalyzerOfRule> analyzers;

    private final PageProcessor pageProcessor;

    public RulesBasedPageConsumer(Set<Rule> rules, PageProcessor pageProcessor) {
        this.analyzers = rules.stream()
                .map(AnalyzerOfRule::createFrom)
                .collect(Collectors.toUnmodifiableSet());
        this.pageProcessor = pageProcessor;
    }

    @Override
    public void consumePage(TextPage page) {
        if (isFinished)
            throw new IllegalStateException("PageConsumer is finished. No more pages can be processed.");
        for (var analyzer : analyzers) {
            pageProcessor.processPageUsing(analyzer, page);
        }
    }

    /**
     * @return all features produced for the rules of this page consumer
     */
    @Override
    public List<Feature> finish() {
        isFinished = true;
        return analyzers.stream().flatMap(analyzerOfRule -> analyzerOfRule.analyzer().findFeatures().stream()).toList();
    }
}