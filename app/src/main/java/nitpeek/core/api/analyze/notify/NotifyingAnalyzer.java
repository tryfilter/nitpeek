package nitpeek.core.api.analyze.notify;

import nitpeek.core.api.analyze.Analyzer;
import nitpeek.core.api.common.Feature;

/**
 * Implement this interface if you want to support sending notifications for found features.<br>
 * This can be useful when complete text processing is expected to be time-consuming and you want to provide fast
 * feedback, or when complete text processing does not have a well-defined finite length.
 * <br>
 * <br>
 * Calling the {@link Analyzer#findFeatures()} method shall return all features (and no more than those) that have so
 * far been published through {@link FeatureNotifier#notifyFeature(Feature)}
 */
public interface NotifyingAnalyzer extends Analyzer, FeatureNotifier {
}
