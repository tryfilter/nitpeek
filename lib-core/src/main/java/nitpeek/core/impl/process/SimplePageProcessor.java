package nitpeek.core.impl.process;

import nitpeek.core.api.analyze.AnalyzerOfRule;
import nitpeek.core.api.common.TextPage;

public final class SimplePageProcessor implements PageProcessor {
    @Override
    public void processPageUsing(AnalyzerOfRule analyzerOfRule, TextPage page) {
        var analyzer = analyzerOfRule.analyzer();
        analyzer.processPage(page);
    }
}