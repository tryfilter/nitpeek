package nitpeek.core.impl.report;

import nitpeek.core.api.common.Feature;
import nitpeek.core.api.report.FeatureFormatter;
import nitpeek.core.api.report.Reporter;
import nitpeek.core.api.report.ReportingException;
import nitpeek.core.api.report.ReportingTarget;

import java.util.List;

public final class SeparatorReporter implements Reporter {

    private final ReportingTarget target;
    private final FeatureFormatter formatter;

    private final String separator;

    public SeparatorReporter(String separator, ReportingTarget target, FeatureFormatter formatter) {
        this.target = target;
        this.formatter = formatter;
        this.separator = separator;
    }

    /**
     * @throws ReportingException when the ReportingTarget provided at construction produced a {@link ReportingException}
     */
    @Override
    public void reportFeatures(List<Feature> features) throws ReportingException {
        if (features.isEmpty()) return;

        for (var feature : features.subList(0, features.size() - 1)) {
            target.report(formatter.format(feature));
            target.report(separator);
        }
        target.report(formatter.format(features.getLast()));
    }
}
