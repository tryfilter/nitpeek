package com.nitpeek.core.impl.process;

import com.nitpeek.core.api.analyze.AnalyzerOfRule;
import com.nitpeek.core.api.common.TextPage;

public interface PageProcessor {
    void processPageUsing(AnalyzerOfRule analyzerOfRule, TextPage page);
}