package com.nitpeek.test.core.impl.process;

import com.nitpeek.core.api.analyze.Analyzer;
import com.nitpeek.core.api.analyze.Rule;
import com.nitpeek.core.api.common.FeatureFilter;
import com.nitpeek.core.api.common.PageAwareFeatureFilter;
import com.nitpeek.core.api.common.TextPage;
import com.nitpeek.core.impl.analyze.SimpleTextPage;
import com.nitpeek.core.impl.common.SimpleIdentifier;
import com.nitpeek.core.impl.process.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
final class RuleSetBasedPageConsumerShould {

    private final TextPage page1 = new SimpleTextPage("test", 1);
    private final TextPage page2 = new SimpleTextPage("test", 2);
    private final TextPage page3 = new SimpleTextPage("test", 3);

    @Mock private PageProcessor pageProcessor;
    @Mock private Analyzer analyzer1;
    @Mock private Analyzer analyzer2;

    @Mock private Rule rule1;
    @Mock private Rule rule2;

    @Mock private PageAwareFeatureFilter featureFilter;

    @Test
    void throwForConsumeAfterFinish() {
        var ruleSetBasedPageConsumer = new RuleSetBasedPageConsumer(Set.of(), pageProcessor);
        ruleSetBasedPageConsumer.consumePage(page1);
        ruleSetBasedPageConsumer.consumePage(page2);
        ruleSetBasedPageConsumer.finish();
        assertThrows(IllegalStateException.class, () -> ruleSetBasedPageConsumer.consumePage(page3));
    }

    @Test
    void processPageForAllRules() {
        when(rule1.createAnalyzer()).thenReturn(analyzer1);
        when(rule2.createAnalyzer()).thenReturn(analyzer2);
        var ruleSetBasedPageConsumer = new RuleSetBasedPageConsumer(
                Set.of(
                        new SimpleRuleSetProvider(
                                Set.of(rule1, rule2),
                                new SimpleIdentifier("-", "-", "-")
                        )
                ), new SimplePageProcessor()
        );

        ruleSetBasedPageConsumer.consumePage(page1);
        ruleSetBasedPageConsumer.consumePage(page2);

        verify(analyzer1).processPage(page1);
        verify(analyzer1).processPage(page2);
        verify(analyzer2).processPage(page1);
        verify(analyzer2).processPage(page2);
    }

    @Test
    void processPagesForFilter() {
        when(rule1.createAnalyzer()).thenReturn(analyzer1);
        when(rule2.createAnalyzer()).thenReturn(analyzer2);

        var ruleSetBasedPageConsumer = new RuleSetBasedPageConsumer(
                Set.of(
                        new SimpleRuleSetProvider(
                                Set.of(rule1, rule2),
                                new SimpleIdentifier("-", "-", "-"),
                                featureFilter
                        )
                ), new SimplePageProcessor()
        );

        ruleSetBasedPageConsumer.consumePage(page1);
        ruleSetBasedPageConsumer.consumePage(page2);
        ruleSetBasedPageConsumer.finish();

        verify(featureFilter).consumePage(page1);
        verify(featureFilter).consumePage(page2);
        verify(featureFilter).finish();

    }
}