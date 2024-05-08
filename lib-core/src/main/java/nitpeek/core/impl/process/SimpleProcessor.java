package nitpeek.core.impl.process;

import nitpeek.core.api.analyze.AnalyzerOfRule;
import nitpeek.core.api.analyze.Rule;
import nitpeek.core.api.common.Feature;
import nitpeek.core.api.common.TextPage;
import nitpeek.core.api.process.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class SimpleProcessor implements FeatureProducingProcessor {

    private Consumer innerConsumer;
    private final Set<Rule> rules;

    public SimpleProcessor(Set<Rule> rules) {
        this.rules = Set.copyOf(rules);
    }

    @Override
    public void startProcessing(PageSource pageSource) {
        innerConsumer = new Consumer();
        pageSource.dischargeTo(innerConsumer);
    }

    @Override
    public List<Feature> getProcessingResult() {
        return innerConsumer.getAllFeatures();
    }

    private final class Consumer implements PageConsumer {
        private boolean isFinished = false;
        private final Set<AnalyzerOfRule> analyzers = rules.stream()
                .map(AnalyzerOfRule::createFrom)
                .collect(Collectors.toUnmodifiableSet());

        @Override
        public void consumePage(TextPage page) {
            if (isFinished)
                throw new IllegalStateException("PageConsumer is finished. No more pages can be processed.");
            for (var analyzer : analyzers) {
                analyzer.analyzer().processPage(page);
            }
        }

        @Override
        public void finish() {
            isFinished = true;
        }

        public List<Feature> getAllFeatures() {
            if (!isFinished)
                throw new IllegalStateException("PageConsumer is not finished. Can't get all features yet.");
            return analyzers.stream().flatMap(analyzerOfRule -> analyzerOfRule.analyzer().findFeatures().stream()).toList();
        }
    }
}