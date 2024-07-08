module com.nitpeek.test.lib.client {
    requires com.nitpeek.lib.core;
    requires com.nitpeek.lib.client;

    // enable tests
    requires org.junit.jupiter.api;
    requires org.mockito;
    requires org.mockito.junit.jupiter;
    requires org.junit.jupiter.params;
    opens com.nitpeek.client.plugin.test to org.junit.platform.commons;
    opens com.nitpeek.client.ruleset.test to org.junit.platform.commons;
    opens com.nitpeek.client.translation.test to org.junit.platform.commons;
}