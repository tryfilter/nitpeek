package nitpeek.core.api.analyze.transformer;

import nitpeek.core.api.common.Feature;

public interface FeatureTransformer {
    Feature transform(Feature original);
}
