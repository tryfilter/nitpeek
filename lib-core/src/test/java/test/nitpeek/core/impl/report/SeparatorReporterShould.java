package test.nitpeek.core.impl.report;

import test.nitpeek.core.impl.analyze.DummyFeature;
import nitpeek.core.api.common.Feature;
import nitpeek.core.api.report.FeatureFormatter;
import nitpeek.core.api.report.Reporter;
import nitpeek.core.impl.report.SeparatorReporter;
import nitpeek.core.impl.report.WriterReportingTarget;
import nitpeek.core.impl.translate.DefaultFallbackEnglishTranslation;
import nitpeek.core.api.translate.Translation;
import nitpeek.core.impl.translate.IdentityTranslation;
import org.junit.jupiter.api.Test;

import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class SeparatorReporterShould {

    private final Translation i18nNoop = new IdentityTranslation();

    private static final List<Feature> features = List.of(
            new DummyFeature("Feature1"),
            new DummyFeature("Feature2"),
            new DummyFeature("Feature3")
    );

    @Test
    void reportNothingForEmptyFeatureList() throws Exception {
        Writer writer = new StringWriter();

        Reporter reporter = new SeparatorReporter("~", new WriterReportingTarget(writer), new DummyFormatter());
        reporter.reportFeatures(List.of(), i18nNoop);

        String expected = "";
        assertEquals(expected, writer.toString());
    }

    @Test
    void reportNoSeparatorForSingleElementList() throws Exception {
        Writer writer = new StringWriter();

        Reporter reporter = new SeparatorReporter("~", new WriterReportingTarget(writer), new DummyFormatter());
        reporter.reportFeatures(List.of(new DummyFeature("Feature1")), i18nNoop);

        String expected = "Feature1";
        assertEquals(expected, writer.toString());
    }
    @Test
    void reportFeatures() throws Exception {
        Writer writer = new StringWriter();

        Reporter reporter = new SeparatorReporter("~", new WriterReportingTarget(writer), new DummyFormatter());
        reporter.reportFeatures(features, i18nNoop);

        String expected = "Feature1~Feature2~Feature3";
        assertEquals(expected, writer.toString());
    }

    @Test
    void reportFeatures2() throws Exception {
        Writer writer = new StringWriter();

        Reporter reporter = new SeparatorReporter("~", new WriterReportingTarget(writer), new DummyFormatter());
        reporter.reportFeatures(features.subList(1, 3), i18nNoop);

        String expected = "Feature2~Feature3";
        assertEquals(expected, writer.toString());
    }

    @Test
    void reportFeaturesWithNewline() throws Exception {
        Writer writer = new StringWriter();


        Reporter reporter = new SeparatorReporter("\n", new WriterReportingTarget(writer), new DummyFormatter());
        reporter.reportFeatures(features, i18nNoop);

        String expected = """
                Feature1
                Feature2
                Feature3""";
        assertEquals(expected, writer.toString());
    }

    private record DummyFormatter() implements FeatureFormatter {
        private static final Translation defaultTranslation = new DefaultFallbackEnglishTranslation();
        @Override
        public String format(Feature feature) {
            return feature.getType().getFeatureId().getName(defaultTranslation);
        }
    }
}
