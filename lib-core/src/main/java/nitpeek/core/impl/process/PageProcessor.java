package nitpeek.core.impl.process;

import nitpeek.core.api.analyze.AnalyzerOfRule;
import nitpeek.core.api.common.TextPage;

public interface PageProcessor {
    void processPageUsing(AnalyzerOfRule analyzerOfRule, TextPage page);
}