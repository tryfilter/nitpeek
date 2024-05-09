package nitpeek.core.impl.process;

import nitpeek.core.api.common.Feature;
import nitpeek.core.api.process.FeatureProducingPageConsumer;
import nitpeek.core.api.process.FeatureProducingProcessor;
import nitpeek.core.api.process.PageSource;

import java.util.ArrayList;
import java.util.List;

public final class SimpleProcessor implements FeatureProducingProcessor {

    private final FeatureProducingPageConsumer consumer;

    private final List<Feature> features = new ArrayList<>();

    public SimpleProcessor(FeatureProducingPageConsumer consumer) {
        this.consumer = consumer;
    }

    @Override
    public void startProcessing(PageSource pageSource) {
        features.addAll(pageSource.dischargeTo(consumer));
    }

    @Override
    public List<Feature> getProcessingResult() {
        return features;
    }
}