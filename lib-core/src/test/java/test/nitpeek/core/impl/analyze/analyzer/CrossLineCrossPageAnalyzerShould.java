package test.nitpeek.core.impl.analyze.analyzer;

import nitpeek.core.api.analyze.Analyzer;
import nitpeek.core.impl.analyze.analyzer.CrossLineCrossPageAnalyzer;
import nitpeek.core.impl.analyze.analyzer.LiteralReplacer;

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
