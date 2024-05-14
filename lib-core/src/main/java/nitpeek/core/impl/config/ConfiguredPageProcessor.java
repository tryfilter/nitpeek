package nitpeek.core.impl.config;

import nitpeek.core.api.analyze.AnalyzerOfRule;
import nitpeek.core.api.analyze.ConfigurableAnalyzer;
import nitpeek.core.api.common.TextPage;
import nitpeek.core.api.config.Configuration;
import nitpeek.core.impl.process.PageProcessor;

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