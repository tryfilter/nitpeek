package nitpeek.core.impl.analyze.analyzer;

import nitpeek.core.api.analyze.FeatureNotifier;
import nitpeek.core.impl.analyze.notify.ThreadsafeFeatureNotifier;

final class ThreadsafeNotifierShould implements FeatureNotifiersShould {

    @Override
    public FeatureNotifier getNotifier() {
        return new ThreadsafeFeatureNotifier();
    }
}
