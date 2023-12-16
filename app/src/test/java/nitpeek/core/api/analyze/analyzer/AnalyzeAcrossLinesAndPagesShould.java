package nitpeek.core.api.analyze.analyzer;

import java.util.List;

final class AnalyzeAcrossLinesAndPagesShould implements CrossPageAnalyzerShould {
    @Override
    public Analyzer setupPageJoiner(String searchTerm) {
        return new AnalyzeAcrossLinesAndPages(new LiteralReplacer(searchTerm, "newValue"));
    }

    @Override
    public List<String> getSeparatorLines() {
        return List.of();
    }
}
