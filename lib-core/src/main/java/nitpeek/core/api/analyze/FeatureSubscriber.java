package nitpeek.core.api.analyze;

import nitpeek.core.api.common.Feature;

public interface FeatureSubscriber {
    void onNextFeature(Feature feature);
}
