package nitpeek.core.api.report;

import nitpeek.core.api.analyze.DummyFeature;
import nitpeek.core.api.common.Feature;
import org.junit.jupiter.api.Test;

import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class ReportWithWriterShould {

    private static final List<Feature> features = List.of(
            new DummyFeature("Feature1"),
            new DummyFeature("Feature2"),
            new DummyFeature("Feature3")
    );

    @Test
    void reportFeatures() throws Exception {
        Writer writer = new StringWriter();

        Reporter reporter = new ReportWithWriter(writer, new DummyFormatter("~"));
        reporter.reportFeatures(features);

        String expected = "Feature1~Feature2~Feature3~";
        assertEquals(expected, writer.toString());
    }

    @Test
    void reportFeaturesWithNewline() throws Exception {
        Writer writer = new StringWriter();


        Reporter reporter = new ReportWithWriter(writer, new DummyFormatter("\n"));
        reporter.reportFeatures(features);

        String expected = """
                Feature1
                Feature2
                Feature3
                """;
        assertEquals(expected, writer.toString());
    }

    private record DummyFormatter(String separator) implements FeatureFormatter {
        @Override
        public String format(Feature feature) {
            return feature.getType().name() + separator;
        }
    }
}
