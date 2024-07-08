package com.nitpeek.test.core.impl.config;

import com.nitpeek.core.api.analyze.Analyzer;
import com.nitpeek.core.api.analyze.AnalyzerOfRule;
import com.nitpeek.core.api.analyze.ConfigurableAnalyzer;
import com.nitpeek.core.api.analyze.RuleType;
import com.nitpeek.core.impl.analyze.SimpleTextPage;
import com.nitpeek.core.impl.config.ConfiguredPageProcessor;
import com.nitpeek.core.impl.config.MapConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
final class ConfiguredPageProcessorShould {

    @Mock private RuleType ruleType;

    @Test
    void configureConfigurableAnalyzers() {
        var configuration = new MapConfiguration(Map.of());

        Analyzer analyzer1 = mock(Analyzer.class);
        ConfigurableAnalyzer analyzer2 = mock(ConfigurableAnalyzer.class);
        ConfigurableAnalyzer analyzer3 = mock(ConfigurableAnalyzer.class);
        var analyzers = List.of(
                new AnalyzerOfRule(analyzer1, ruleType),
                new AnalyzerOfRule(analyzer2, ruleType),
                new AnalyzerOfRule(analyzer3, ruleType)
        );

        when(analyzer2.permitsFurtherConfiguration()).thenReturn(true);
        when(analyzer3.permitsFurtherConfiguration()).thenReturn(true);

        var configuredPageProcessor = new ConfiguredPageProcessor(configuration);
        var page = new SimpleTextPage("test", 1);
        for (var analyzer : analyzers) {
            configuredPageProcessor.processPageUsing(analyzer, page);
        }

        verify(analyzer2).configure(configuration);
        verify(analyzer3).configure(configuration);
    }
}