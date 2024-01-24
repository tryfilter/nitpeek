module nitpeek.lib.client.test {
    requires nitpeek.lib.core;
    requires nitpeek.lib.client;

    // enable tests
    requires org.junit.jupiter.api;
    opens nitpeek.client.plugin.test to org.junit.platform.commons;
}