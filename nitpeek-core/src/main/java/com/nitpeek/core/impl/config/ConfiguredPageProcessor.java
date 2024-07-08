package com.nitpeek.core.impl.config;

import com.nitpeek.core.api.analyze.AnalyzerOfRule;
import com.nitpeek.core.api.analyze.ConfigurableAnalyzer;
import com.nitpeek.core.api.common.TextPage;
import com.nitpeek.core.api.config.Configuration;
import com.nitpeek.core.impl.process.PageProcessor;

public final class ConfiguredPageProcessor implements PageProcessor {

    private final Configuration configuration;

    public ConfiguredPageProcessor(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void processPageUsing(AnalyzerOfRule analyzerOfRule, TextPage page) {
        var analyzer = analyzerOfRule.analyzer();
        if (analyzer instanceof ConfigurableAnalyzer configurableAnalyzer && configurableAnalyzer.permitsFurtherConfiguration()) {
            configurableAnalyzer.configure(configuration);
        }

        analyzer.processPage(page);
    }
}