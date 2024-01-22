package nitpeek.core.impl.analyze.notify;

import nitpeek.core.api.common.Feature;
import nitpeek.core.api.analyze.FeatureNotifier;
import nitpeek.core.api.analyze.FeatureSubscriber;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public final class ThreadsafeFeatureNotifier implements FeatureNotifier {

    private final Set<FeatureSubscriber> subscribers = new CopyOnWriteArraySet<>();

    @Override
    public boolean subscribe(FeatureSubscriber subscriber) {
        return subscribers.add(subscriber);
    }

    @Override
    public void unSubscribe(FeatureSubscriber subscriber) {
        subscribers.remove(subscriber);
    }

    @Override
    public int notifyFeature(Feature feature) {
        int notificationCount = 0;

        for (var subscriber : subscribers) {
            subscriber.onNextFeature(feature);
            notificationCount++;
        }

        return notificationCount;
    }
}