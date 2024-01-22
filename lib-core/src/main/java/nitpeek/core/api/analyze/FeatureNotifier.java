package nitpeek.core.api.analyze;

import nitpeek.core.api.common.Feature;

public interface FeatureNotifier {

    /**
     * Attempt to subscribe to this notifier
     *
     * @param subscriber the object that is interested in receiving notifications
     * @return {@code true} if this notifier accepted the new subscriber, {@code false} otherwise.
     */
    boolean subscribe(FeatureSubscriber subscriber);

    void unSubscribe(FeatureSubscriber subscriber);

    /**
     * Notify all subscribers that a new feature has been found
     *
     * @return the number of notifications sent
     */

    int notifyFeature(Feature feature);
}
