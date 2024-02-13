package test.nitpeek.core.impl.process;

import nitpeek.core.api.process.RuleSetProvider;
import nitpeek.core.impl.analyze.SimpleRule;
import nitpeek.core.impl.analyze.SimpleRuleType;
import nitpeek.core.impl.analyze.analyzer.LiteralReplacer;
import nitpeek.core.impl.common.SimpleIdentifier;
import nitpeek.core.impl.process.SimpleProcessor;
import nitpeek.core.impl.process.SimpleRuleSetProvider;
import nitpeek.core.impl.process.StringPageSource;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SimpleProcessorShould {

    private static final RuleSetProvider ruleSetProvider = new SimpleRuleSetProvider(
            Set.of(
                    new SimpleRule(
                            new SimpleRuleType("test.REPLACE_TEST_WITH_FEST", "dummy-rule", "Simple dummy rule"),
                            () -> new LiteralReplacer("TEST", "FEST")
                    )
            ), new SimpleIdentifier("dummy-ruleset", "dummy-rule-set", "Simple dummy rule-set")
    );

    @Test
    void produceNoFeaturesWithEmptyPageSource() {

        var processor = new SimpleProcessor(ruleSetProvider);
        var pageSource = new StringPageSource(List.of());

        processor.startProcessing(pageSource);
        assertTrue(processor.getFeatures().isEmpty());
    }

    @Test
    void produceFeatures() {

        var processor = new SimpleProcessor(ruleSetProvider);
        var pageSource = new StringPageSource("TEST 1, 2, 3, TESTTEST");

        processor.startProcessing(pageSource);
        assertEquals(3, processor.getFeatures().size());
    }

    @Test
    void produceSameFeaturesOnRepeatedExtraction() {

        var processor = new SimpleProcessor(ruleSetProvider);
        var pageSource = new StringPageSource("TEST 1, 2, 3, TESTTEST");

        processor.startProcessing(pageSource);
        var first = processor.getFeatures();
        var second = processor.getFeatures();
        assertEquals(first, second);
    }

    @Test
    void produceSameFeaturesOnRepeatedProcessing() {

        var processor = new SimpleProcessor(ruleSetProvider);
        var pageSource = new StringPageSource("TEST 1, 2, 3, TESTTEST");

        processor.startProcessing(pageSource);
        var first = processor.getFeatures();
        processor.startProcessing(pageSource);
        var second = processor.getFeatures();
        assertEquals(first, second);
    }
}