package com.nitpeek.test.core.impl.analyze.analyzer;

import com.nitpeek.core.api.analyze.Analyzer;
import com.nitpeek.core.impl.analyze.analyzer.CrossLineCrossPageAnalyzer;
import com.nitpeek.core.impl.analyze.analyzer.LiteralReplacer;

import java.util.List;

final class CrossLineCrossPageAnalyzerShould implements CrossPageAnalyzersShould {
    @Override
    public Analyzer setupPageJoiner(String searchTerm) {
        return new CrossLineCrossPageAnalyzer(new LiteralReplacer(searchTerm, "newValue"));
    }

    @Override
    public List<String> getSeparatorLines() {
        return List.of();
    }
}
