package nitpeek.core.api.analyze.analyzer;

import nitpeek.core.api.analyze.TextPage;
import nitpeek.core.api.analyze.notify.FeatureNotifier;
import nitpeek.core.api.analyze.notify.FeatureSubscriber;
import nitpeek.core.api.analyze.notify.NotifyingAnalyzer;
import nitpeek.core.api.analyze.notify.ThreadsafeFeatureNotifier;
import nitpeek.core.api.common.Feature;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A convenience decorator for adding notification capabilities to existing analyzers.
 */
public final class ObservableAnalyzer implements NotifyingAnalyzer {

    private final Analyzer analyzer;
    private final FeatureNotifier notifier;

    private Set<Feature> featuresAtPreviousProcessPageCall = new HashSet<>();

    /**
     * @param analyzer the analyzer to decorate. If it implements FeatureNotifier, notification responsibilities will be
     *                 delegated to it.
     */
    public ObservableAnalyzer(Analyzer analyzer) {
        this.analyzer = analyzer;

        if (analyzer instanceof FeatureNotifier featureNotifier) this.notifier = featureNotifier;
        else this.notifier = new ThreadsafeFeatureNotifier();
    }

    public ObservableAnalyzer(Analyzer analyzer, FeatureNotifier notifier) {
        this.analyzer = analyzer;
        this.notifier = notifier;
    }

    /**
     * @return an unmodifiable snapshot
     */
    @Override
    public List<Feature> findFeatures() {
        return List.copyOf(analyzer.findFeatures());
    }

    @Override
    public void processPage(TextPage page) {

        analyzer.processPage(page);

        // If the notifier is the same object as the analyzer, that means the analyzer supports notifications out of
        // the box and knows when to send them. If so, we don't need any additional logic to ensure timely
        // notification dispatch
        if (analyzer == notifier) return;

        ensureTimelyNotifications();
    }

    private void ensureTimelyNotifications() {

        List<Feature> currentFeatures = analyzer.findFeatures();
        for (var feature : currentFeatures) {
            if (!featuresAtPreviousProcessPageCall.contains(feature)) {
                notifier.notifyFeature(feature);
            }
        }

        featuresAtPreviousProcessPageCall = new HashSet<>(currentFeatures);
    }

    @Override
    public boolean subscribe(FeatureSubscriber subscriber) {
        return notifier.subscribe(subscriber);
    }

    @Override
    public void unSubscribe(FeatureSubscriber subscriber) {
        notifier.unSubscribe(subscriber);
    }

    @Override
    public int notifyFeature(Feature feature) {
        return notifier.notifyFeature(feature);
    }
}