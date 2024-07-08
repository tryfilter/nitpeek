package com.nitpeek.test.core.impl.analyze.analyzer;

import com.nitpeek.core.api.common.TextPage;
import com.nitpeek.core.api.analyze.NotifyingAnalyzer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

interface NotifyingAnalyzersShould extends FeatureNotifiersShould {

    NotifyingAnalyzer getAnalyzer();
    List<TextPage> getPages();

    @Test
    default void sendFeaturesNoLaterThanAvailableInFindFeatures() {
        var analyzer = getAnalyzer();
        var subscriber = new RecordingSubscriber();

        analyzer.subscribe(subscriber);

        for (var page : getPages()) {
            analyzer.processPage(page);
            var expected = analyzer.findFeatures();
            var actual = subscriber.getRecording();

            Assertions.assertEquals(expected, actual);
        }

    }

}

