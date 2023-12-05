package nitpeek.core.api.report;

import java.io.IOException;
import java.io.Writer;

public final class WriterReportingTarget implements ReportingTarget {

    private final Writer writer;

    public WriterReportingTarget(Writer writer) {
        this.writer = writer;
    }

    @Override
    public void report(String string) throws ReportingException {
        try {
            writer.append(string);
            writer.flush();
        } catch (IOException e) {
            throw new ReportingException(e);
        }
    }
}
