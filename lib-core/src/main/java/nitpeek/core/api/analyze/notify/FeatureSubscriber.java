package nitpeek.core.api.analyze.notify;

import nitpeek.core.api.common.Feature;

public interface FeatureSubscriber {
    void onNextFeature(Feature feature);
}
