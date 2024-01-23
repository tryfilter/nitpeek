package nitpeek.core.impl.analyze.analyzer;

import nitpeek.core.api.analyze.Analyzer;

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
