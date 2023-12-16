package nitpeek.core.api.analyze.analyzer;

import java.util.List;

final class PageJoinerShould implements CrossPageAnalyzerShould {

    private static final List<String> separatorLines = List.of("Delimiter line #1", "Delimiter line #2 or something");
    @Override
    public Analyzer setupPageJoiner(String searchTerm) {
        return new PageJoiner(
                () -> new AnalyzeAcrossLines(new LiteralReplacer(searchTerm, "new")),
                separatorLines
        );
    }

    @Override
    public List<String> getSeparatorLines() {
        return separatorLines;
    }
}
