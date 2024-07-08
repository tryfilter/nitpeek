package com.nitpeek.test.core.impl.analyze.analyzer;

import com.nitpeek.test.core.impl.analyze.DummyFeature;
import com.nitpeek.core.api.analyze.FeatureNotifier;
import com.nitpeek.core.api.common.Feature;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

interface FeatureNotifiersShould {

    String DUMMY = "dummy";
    Feature dummyFeature = new DummyFeature(DUMMY);

    FeatureNotifier getNotifier();

    @Test
    default void notifyAllSubscribersAndReturnSubscriberCount() {
        var notifier = getNotifier();
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
    default void notNotifyAfterUnsubscribing() {
        var notifier = getNotifier();
        var subscriber = new RecordingSubscriber();

        notifier.subscribe(subscriber);
        notifier.unSubscribe(subscriber);

        int notificationCount = notifier.notifyFeature(dummyFeature);

        var expectedFeatureNames = List.of();

        assertEquals(0, notificationCount);
        assertEquals(expectedFeatureNames, subscriber.getRecording());
    }

    @Test
    default void respectNotificationOrder() {
        var notifier = getNotifier();
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
    default void notAcceptSameSubscriberTwice() {
        var notifier = getNotifier();
        var subscriber = new RecordingSubscriber();
        notifier.subscribe(subscriber);
        boolean accepted = notifier.subscribe(subscriber);

        assertFalse(accepted);

        int notificationCount = notifier.notifyFeature(dummyFeature);

        assertEquals(1, notificationCount);
    }

    @Test
    default void return0WhenNotifyingWhileNoneSubscribed() {
        var notifier = getNotifier();
        assertEquals(0, notifier.notifyFeature(dummyFeature));
    }
}
