package test.nitpeek.core.impl.config;

import nitpeek.core.api.common.Feature;
import nitpeek.core.api.common.TextPage;
import nitpeek.core.api.config.Configuration;
import nitpeek.core.impl.config.MapConfiguration;
import nitpeek.core.impl.config.OnceConfigurableAnalyzer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

final class OnceConfigurableAnalyzerShould {

    private OnceConfigurableAnalyzer onceConfigurableAnalyzer;

    @BeforeEach
    void setup() {
        onceConfigurableAnalyzer = spy(new OnceConfigurableAnalyzer() {
            @Override
            public void acceptConfiguration(Configuration configuration) {
            }

            @Override
            public List<Feature> findFeatures() {
                return null;
            }

            @Override
            public void processPage(TextPage page) {
            }
        });
    }

    @Test
    void permitInitialConfiguration() {
        Assertions.assertTrue(onceConfigurableAnalyzer.permitsFurtherConfiguration());
    }

    @Test
    void notPermitSubsequentConfiguration() {
        onceConfigurableAnalyzer.configure(new MapConfiguration(Map.of()));
        Assertions.assertFalse(onceConfigurableAnalyzer.permitsFurtherConfiguration());
    }

    @Test
    void notAcceptSubsequentConfiguration() {
        onceConfigurableAnalyzer.configure(new MapConfiguration(Map.of()));
        onceConfigurableAnalyzer.configure(new MapConfiguration(Map.of()));

        verify(onceConfigurableAnalyzer).acceptConfiguration(any());
    }
}