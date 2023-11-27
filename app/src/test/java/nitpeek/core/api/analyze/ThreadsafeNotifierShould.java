package nitpeek.core.api.analyze;

import nitpeek.core.api.analyze.notify.FeatureNotifier;
import nitpeek.core.api.analyze.notify.ThreadsafeFeatureNotifier;
import nitpeek.core.api.common.Feature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

final class ThreadsafeNotifierShould {

    private static final String DUMMY = "dummy";
    private final Feature dummyFeature = new DummyFeature(DUMMY);
    private FeatureNotifier notifier;


    @BeforeEach
    void setup() {
        notifier = new ThreadsafeFeatureNotifier();
    }

    @Test
    void return0WhenNotifyingWhileNoneSubscribed() {
        assertEquals(0, notifier.notifyFeature(dummyFeature));
    }

    @Test
    void notifyAllSubscribersAndReturnSubscriberCount() {
        var subscriber1 = new RecordingSubscriber();
        var subscriber2 = new RecordingSubscriber();

        notifier.subscribe(subscriber1);
        notifier.subscribe(subscriber2);

        int notificationCount = notifier.notifyFeature(dummyFeature);

        var expectedFeatureNames = List.of(dummyFeature);

        assertEquals(2, notificationCount);
        assertEquals(expectedFeatureNames, subscriber1.getRecording());
        assertEquals(expectedFeatureNames, subscriber2.getRecording());
    }

    @Test
    void notNotifyAfterUnsubscribing() {
        var subscriber = new RecordingSubscriber();

        notifier.subscribe(subscriber);
        notifier.unSubscribe(subscriber);

        int notificationCount = notifier.notifyFeature(dummyFeature);

        var expectedFeatureNames = List.of();

        assertEquals(0, notificationCount);
        assertEquals(expectedFeatureNames, subscriber.getRecording());
    }

    @Test
    void respectNotificationOrder() {
        var subscriber = new RecordingSubscriber();
        notifier.subscribe(subscriber);

        var features = List.of(
                new DummyFeature("a"),
                new DummyFeature("b"),
                new DummyFeature("c"),
                new DummyFeature("d")
        );

        for (var feature : features) {
            notifier.notifyFeature(feature);
        }

        assertEquals(features, subscriber.getRecording());
    }

    @Test
    void notAcceptSameSubscriberTwice() {
        var subscriber = new RecordingSubscriber();
        notifier.subscribe(subscriber);
        boolean accepted = notifier.subscribe(subscriber);

        assertFalse(accepted);

        int notificationCount = notifier.notifyFeature(dummyFeature);

        assertEquals(1, notificationCount);
    }
}
