module nitpeek.lib.client.test {
    requires nitpeek.lib.core;
    requires nitpeek.lib.client;

    // enable tests
    requires org.junit.jupiter.api;
    requires org.mockito;
    requires org.mockito.junit.jupiter;
    opens nitpeek.client.plugin.test to org.junit.platform.commons;
    opens nitpeek.client.translation.test to org.junit.platform.commons;
}