package com.nitpeek.core.impl.report;

import com.nitpeek.core.api.report.ReportingException;
import com.nitpeek.core.api.report.ReportingTarget;

import java.io.IOException;
import java.io.Writer;

import static com.nitpeek.core.api.report.ReportingException.*;

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
            throw new ReportingException(e, Problem.OUTPUT);
        }
    }
}