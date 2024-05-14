package nitpeek.core.impl.config;

import nitpeek.core.api.analyze.ConfigurableAnalyzer;
import nitpeek.core.api.config.Configuration;

public abstract class OnceConfigurableAnalyzer implements ConfigurableAnalyzer {

    private boolean isConfigured = false;

    public abstract void acceptConfiguration(Configuration configuration);
    @Override
    public final void configure(Configuration configuration) {
        if (!permitsFurtherConfiguration()) return;
        acceptConfiguration(configuration);
        isConfigured = true;
    }

    @Override
    public final boolean permitsFurtherConfiguration() {
        return !isConfigured;
    }
}