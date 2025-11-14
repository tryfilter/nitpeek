package com.nitpeek.core.impl.process;

import com.nitpeek.core.api.analyze.AnalyzerOfRule;
import com.nitpeek.core.api.common.Feature;
import com.nitpeek.core.api.common.FeatureFilter;
import com.nitpeek.core.api.common.PageAwareFeatureFilter;
import com.nitpeek.core.api.common.TextPage;
import com.nitpeek.core.api.process.FeatureProducingPageConsumer;
import com.nitpeek.core.api.process.RuleSetProvider;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Since this page consumer has access to the full RuleSetProvider, it will also apply the associated filters, if any.
 */

public final class RuleSetBasedPageConsumer implements FeatureProducingPageConsumer {
    private boolean isFinished = false;

    private final Set<RuleSetComponents> ruleSetProviders;

    private final PageProcessor pageProcessor;

    private record RuleSetComponents(FeatureFilter filter, Set<AnalyzerOfRule> analyzers) {
    }

    public RuleSetBasedPageConsumer(Set<RuleSetProvider> ruleSetProviders, PageProcessor pageProcessor) {
        this.pageProcessor = pageProcessor;
        this.ruleSetProviders = ruleSetProviders.stream()
                .map(rsp ->
                        new RuleSetComponents(
                                rsp.getFilter(),
                                rsp.getRules().stream()
                                        .map(AnalyzerOfRule::createFrom)
                                        .collect(Collectors.toUnmodifiableSet())
                        )
                )
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public void consumePage(TextPage page) {
        if (isFinished)
            throw new IllegalStateException("PageConsumer is finished. No more pages can be processed.");

        for (var ruleSetComponents : ruleSetProviders) {
            for (var analyzer : ruleSetComponents.analyzers()) {
                pageProcessor.processPageUsing(analyzer, page);
            }
            if (ruleSetComponents.filter instanceof PageAwareFeatureFilter pageAwareFeatureFilter)
                pageAwareFeatureFilter.consumePage(page);
        }
    }

    /**
     * This method also calls finish on any filters that support it
     *
     * @return aggregated features produced by all rules of each of the component RuleSetProviders, whose FeatureFilter
     * is used to filter its own features before they are aggregated
     */
    @Override
    public List<Feature> finish() {
        isFinished = true;
        for (var ruleSetComponents : ruleSetProviders)
            if (ruleSetComponents.filter instanceof PageAwareFeatureFilter pageAwareFeatureFilter)
                pageAwareFeatureFilter.finish();

        return ruleSetProviders.stream()
                .flatMap(ruleSetComponents ->
                        ruleSetComponents.analyzers().stream()
                                .flatMap(analyzerOfRule -> analyzerOfRule.analyzer().findFeatures().stream())
                                .filter(ruleSetComponents.filter)
                )
                .toList();
    }
}