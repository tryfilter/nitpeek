package nitpeek.core.api.analyze;

import nitpeek.core.api.analyze.notify.FeatureNotifier;
import nitpeek.core.api.analyze.notify.ThreadsafeFeatureNotifier;

final class ThreadsafeNotifierShould implements FeatureNotifiersShould {

    @Override
    public FeatureNotifier getNotifier() {
        return new ThreadsafeFeatureNotifier();
    }
}
