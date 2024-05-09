package test.nitpeek.core.impl.process;

import nitpeek.core.api.analyze.Analyzer;
import nitpeek.core.api.analyze.Rule;
import nitpeek.core.api.common.TextPage;
import nitpeek.core.impl.analyze.SimpleTextPage;
import nitpeek.core.impl.process.PageProcessor;
import nitpeek.core.impl.process.RulesBasedPageConsumer;
import nitpeek.core.impl.process.SimplePageProcessor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
final class RulesBasedPageConsumerShould {


    private final TextPage page1 = new SimpleTextPage("test", 1);
    private final TextPage page2 = new SimpleTextPage("test", 2);
    private final TextPage page3 = new SimpleTextPage("test", 3);


    @Mock private PageProcessor pageProcessor;
    @Mock private Analyzer analyzer1;
    @Mock private Analyzer analyzer2;

    @Mock private Rule rule1;
    @Mock private Rule rule2;

    @Test
    void throwForConsumeAfterFinish() {
        var rulesBasedPageConsumer = new RulesBasedPageConsumer(Set.of(), pageProcessor);
        rulesBasedPageConsumer.consumePage(page1);
        rulesBasedPageConsumer.consumePage(page2);
        rulesBasedPageConsumer.finish();
        assertThrows(IllegalStateException.class, () -> rulesBasedPageConsumer.consumePage(page3));
    }

    @Test
    void processPageForAllRules() {
        when(rule1.createAnalyzer()).thenReturn(analyzer1);
        when(rule2.createAnalyzer()).thenReturn(analyzer2);
        var rulesBasedPageConsumer = new RulesBasedPageConsumer(Set.of(rule1, rule2), new SimplePageProcessor());

        rulesBasedPageConsumer.consumePage(page1);
        rulesBasedPageConsumer.consumePage(page2);

        verify(analyzer1).processPage(page1);
        verify(analyzer1).processPage(page2);
        verify(analyzer2).processPage(page1);
        verify(analyzer2).processPage(page2);
    }
}