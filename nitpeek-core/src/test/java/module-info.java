module com.nitpeek.test.lib.core {
    requires com.nitpeek.lib.core;

    // enable tests
    requires org.junit.jupiter.api;
    requires org.junit.jupiter.params;
    requires org.mockito;
    requires org.mockito.junit.jupiter;
    opens com.nitpeek.test.core.impl.analyze to org.junit.platform.commons;
    opens com.nitpeek.test.core.impl.analyze.analyzer to org.junit.platform.commons;
    opens com.nitpeek.test.core.impl.analyze.transformer to org.junit.platform.commons;
    opens com.nitpeek.test.core.impl.common to org.junit.platform.commons;
    opens com.nitpeek.test.core.impl.process to org.junit.platform.commons;
    opens com.nitpeek.test.core.impl.config to org.junit.platform.commons;
    opens com.nitpeek.test.core.impl.report to org.junit.platform.commons;
    opens com.nitpeek.test.core.impl.translate to org.junit.platform.commons;
}