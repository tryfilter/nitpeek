package nitpeek.core.api.analyze.notify;

import nitpeek.core.api.common.Feature;

import java.util.*;

/**
 * Utility subscriber implementation that records notification actions as a List of the features' names.
 */
final class RecordingSubscriber implements FeatureSubscriber {

    private final List<String> recordingTarget = new ArrayList<>();


    @Override
    public void onNextFeature(Feature feature) {
        recordingTarget.add(feature.getType().name());
    }

    public List<String> getRecording() {
        return Collections.unmodifiableList(recordingTarget);
    }
}
