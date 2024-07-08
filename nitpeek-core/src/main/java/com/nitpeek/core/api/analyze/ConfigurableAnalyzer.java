package com.nitpeek.core.api.analyze;

import com.nitpeek.core.api.config.Configuration;


public interface ConfigurableAnalyzer extends Analyzer {

    /**
     * Configure this analyzer using the provided {@code configuration}.
     * This should generally be done before any page processing occurs.
     */
    void configure(Configuration configuration);

    /**
     * @return true if this analyzer supports overwriting its current configuration, false otherwise
     */
    boolean permitsFurtherConfiguration();
}