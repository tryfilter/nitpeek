package com.nitpeek.core.api.analyze;

import com.nitpeek.core.api.common.Feature;

public interface FeatureSubscriber {
    void onNextFeature(Feature feature);
}
