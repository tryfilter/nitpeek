package com.nitpeek.core.api.report;

public interface ReportingTarget {

    void report(String string) throws ReportingException;
}
