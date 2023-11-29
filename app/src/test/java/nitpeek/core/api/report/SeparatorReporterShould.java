package nitpeek.core.api.report;

import nitpeek.core.api.analyze.DummyFeature;
import nitpeek.core.api.common.Feature;
import org.junit.jupiter.api.Test;

import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class SeparatorReporterShould {

    private static final List<Feature> features = List.of(
            new DummyFeature("Feature1"),
            new DummyFeature("Feature2"),
            new DummyFeature("Feature3")
    );

    @Test
    void reportNothingForEmptyFeatureList() throws Exception {
        Writer writer = new StringWriter();

        Reporter reporter = new SeparatorReporter("~", new WriterReporter(writer), new DummyFormatter());
        reporter.reportFeatures(List.of());

        String expected = "";
        assertEquals(expected, writer.toString());
    }

    @Test
    void reportNoSeparatorForSingleElementList() throws Exception {
        Writer writer = new StringWriter();

        Reporter reporter = new SeparatorReporter("~", new WriterReporter(writer), new DummyFormatter());
        reporter.reportFeatures(List.of(new DummyFeature("Feature1")));

        String expected = "Feature1";
        assertEquals(expected, writer.toString());
    }
    @Test
    void reportFeatures() throws Exception {
        Writer writer = new StringWriter();

        Reporter reporter = new SeparatorReporter("~", new WriterReporter(writer), new DummyFormatter());
        reporter.reportFeatures(features);

        String expected = "Feature1~Feature2~Feature3";
        assertEquals(expected, writer.toString());
    }

    @Test
    void reportFeatures2() throws Exception {
        Writer writer = new StringWriter();

        Reporter reporter = new SeparatorReporter("~", new WriterReporter(writer), new DummyFormatter());
        reporter.reportFeatures(features.subList(1, 3));

        String expected = "Feature2~Feature3";
        assertEquals(expected, writer.toString());
    }

    @Test
    void reportFeaturesWithNewline() throws Exception {
        Writer writer = new StringWriter();


        Reporter reporter = new SeparatorReporter("\n", new WriterReporter(writer), new DummyFormatter());
        reporter.reportFeatures(features);

        String expected = """
                Feature1
                Feature2
                Feature3""";
        assertEquals(expected, writer.toString());
    }

    private record DummyFormatter() implements FeatureFormatter {
        @Override
        public String format(Feature feature) {
            return feature.getType().name();
        }
    }
}
