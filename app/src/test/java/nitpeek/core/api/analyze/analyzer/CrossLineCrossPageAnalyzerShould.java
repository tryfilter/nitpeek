package nitpeek.core.api.analyze.analyzer;

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
