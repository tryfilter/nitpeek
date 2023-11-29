package nitpeek.core.api.report;

import nitpeek.core.api.common.Feature;

import java.util.List;

public final class SeparatorReporter implements Reporter {

    private final StringReporter reporter;
    private final FeatureFormatter formatter;

    private final String separator;

    public SeparatorReporter(String separator, StringReporter reporter, FeatureFormatter formatter) {
        this.reporter = reporter;
        this.formatter = formatter;
        this.separator = separator;
    }

    /**
     * @throws ReportingException when the provided StringReporter produced a {@link ReportingException}
     */
    @Override
    public void reportFeatures(List<Feature> features) throws ReportingException {
        if (features.isEmpty()) return;

        for (var feature : features.subList(0, features.size() - 1)) {
            reporter.report(formatter.format(feature));
            reporter.report(separator);
        }
        reporter.report(formatter.format(features.getLast()));
    }
}
