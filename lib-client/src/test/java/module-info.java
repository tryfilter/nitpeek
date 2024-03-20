module test.nitpeek.lib.client {
    requires nitpeek.lib.core;
    requires nitpeek.lib.client;

    // enable tests
    requires org.junit.jupiter.api;
    requires org.mockito;
    requires org.mockito.junit.jupiter;
    requires org.junit.jupiter.params;
    opens nitpeek.client.plugin.test to org.junit.platform.commons;
    opens nitpeek.client.ruleset.test to org.junit.platform.commons;
    opens nitpeek.client.translation.test to org.junit.platform.commons;
}