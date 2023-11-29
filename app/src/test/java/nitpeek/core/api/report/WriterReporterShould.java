package nitpeek.core.api.report;

import org.junit.jupiter.api.Test;

import java.io.StringWriter;
import java.io.Writer;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class WriterReporterShould {

    @Test
    void reportStrings() throws ReportingException {
        String s1 = "s1";
        String s2 = "s2";
        String s3 = "s3";

        Writer writer = new StringWriter();
        StringReporter reporter = new WriterReporter(writer);
        reporter.report(s1);
        reporter.report(s2);
        reporter.report(s3);

        String expected = s1 + s2 + s3;
        String actual = writer.toString();

        assertEquals(expected, actual);
    }
}
