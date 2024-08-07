package com.nitpeek.core.impl.report;

import com.nitpeek.core.api.common.Feature;
import com.nitpeek.core.api.report.FeatureFormatter;
import com.nitpeek.core.api.report.Reporter;
import com.nitpeek.core.api.report.ReportingException;
import com.nitpeek.core.api.report.ReportingTarget;
import com.nitpeek.core.api.translate.Translation;

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
    public void reportFeatures(List<Feature> features, Translation i18n) throws ReportingException {
        if (features.isEmpty()) return;

        for (var feature : features.subList(0, features.size() - 1)) {
            target.report(formatter.format(feature));
            target.report(separator);
        }
        target.report(formatter.format(features.getLast()));
    }
}
