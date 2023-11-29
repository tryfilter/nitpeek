package nitpeek.core.api.report;

import nitpeek.core.api.common.Feature;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class ReportWithWriter implements Reporter {

    private final Writer writer;
    private final FeatureFormatter formatter;

    public ReportWithWriter(Writer writer, FeatureFormatter formatter) {
        this.writer = writer;
        this.formatter = formatter;
    }

    /**
     * @throws ReportingException when the provided writer produced an {@link IOException}
     */
    @Override
    public void reportFeatures(List<Feature> features) throws ReportingException {
        for (var feature : features) {
            try {
                writer.append(formatter.format(feature));
            } catch (IOException e) {
                throw new ReportingException(e);
            }
        }

    }
}
