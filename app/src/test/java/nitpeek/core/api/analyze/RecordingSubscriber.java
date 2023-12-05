package nitpeek.core.api.analyze;

import nitpeek.core.api.analyze.notify.FeatureSubscriber;
import nitpeek.core.api.common.Feature;

import java.util.*;

/**
 * Utility subscriber implementation that records notification actions as a List of the features' names.
 */
final class RecordingSubscriber implements FeatureSubscriber {

    private final List<Feature> recordingTarget = new ArrayList<>();


    @Override
    public void onNextFeature(Feature feature) {
        recordingTarget.add(feature);
    }

    /**
     * @return an unmodifiable snapshot
     */
    public List<Feature> getRecording() {
        return List.copyOf(recordingTarget);
    }
}
