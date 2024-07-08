package com.nitpeek.test.core.impl.analyze.analyzer;

import com.nitpeek.core.api.analyze.Analyzer;
import com.nitpeek.core.impl.analyze.analyzer.CrossLineAnalyzer;
import com.nitpeek.core.impl.analyze.analyzer.CrossPageAnalyzer;
import com.nitpeek.core.impl.analyze.analyzer.LiteralReplacer;

import java.util.List;

final class CrossPageAnalyzerShould implements CrossPageAnalyzersShould {

    private static final List<String> separatorLines = List.of("Delimiter line #1", "Delimiter line #2 or something");
    @Override
    public Analyzer setupPageJoiner(String searchTerm) {
        return new CrossPageAnalyzer(
                () -> new CrossLineAnalyzer(new LiteralReplacer(searchTerm, "new")),
                separatorLines
        );
    }

    @Override
    public List<String> getSeparatorLines() {
        return separatorLines;
    }
}
