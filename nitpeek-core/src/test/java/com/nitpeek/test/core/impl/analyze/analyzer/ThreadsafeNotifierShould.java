package com.nitpeek.test.core.impl.analyze.analyzer;

import com.nitpeek.core.api.analyze.FeatureNotifier;
import com.nitpeek.core.impl.analyze.notify.ThreadsafeFeatureNotifier;

final class ThreadSafeNotifierShould implements FeatureNotifiersShould {

    @Override
    public FeatureNotifier getNotifier() {
        return new ThreadsafeFeatureNotifier();
    }
}
