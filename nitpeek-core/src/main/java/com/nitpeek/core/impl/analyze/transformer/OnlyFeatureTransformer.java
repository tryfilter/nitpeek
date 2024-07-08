package com.nitpeek.core.impl.analyze.transformer;

import com.nitpeek.core.api.common.Feature;
import com.nitpeek.core.api.common.TextPage;
import com.nitpeek.core.api.util.FeatureTransformer;
import com.nitpeek.core.api.util.Transformer;

/**
 * Transformer that passes through page contents as is and only modifies features produced by the analyzer.
 */
public final class OnlyFeatureTransformer implements Transformer {

    private final FeatureTransformer featureTransformer;

    public OnlyFeatureTransformer(FeatureTransformer featureTransformer) {
        this.featureTransformer = featureTransformer;
    }

    @Override
    public TextPage transformPage(TextPage original) {
        return original;
    }

    @Override
    public Feature transformFeature(Feature original) {
        return featureTransformer.transform(original);
    }
}
