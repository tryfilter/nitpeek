module test.nitpeek.lib.core {
    requires nitpeek.lib.core;

    // enable tests
    requires org.junit.jupiter.api;
    requires org.junit.jupiter.params;
    requires org.mockito;
    requires org.mockito.junit.jupiter;
    opens test.nitpeek.core.impl.analyze to org.junit.platform.commons;
    opens test.nitpeek.core.impl.analyze.analyzer to org.junit.platform.commons;
    opens test.nitpeek.core.impl.analyze.transformer to org.junit.platform.commons;
    opens test.nitpeek.core.impl.common to org.junit.platform.commons;
    opens test.nitpeek.core.impl.process to org.junit.platform.commons;
    opens test.nitpeek.core.impl.report to org.junit.platform.commons;
    opens test.nitpeek.core.impl.translate to org.junit.platform.commons;
}