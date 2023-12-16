package nitpeek.core.api.analyze.analyzer;

import nitpeek.core.api.analyze.SimpleTextPage;
import nitpeek.core.api.analyze.TextPage;
import nitpeek.core.api.common.TextCoordinate;
import nitpeek.core.api.common.TextSelection;
import nitpeek.core.testutil.FeatureAssert;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

final class PageJoinerShould {

    private Analyzer pageJoiner;

    private static final TextPage pageOne = new SimpleTextPage(List.of(
            "Line one.",
            "Line two.",
            "line three"
    ), 4);
    private static final TextPage pageTwo = new SimpleTextPage(List.of(
            "Another line that",
            "Goes over ",
            "multiple lines..."
    ), 8);
    private static final TextPage pageThree = new SimpleTextPage(List.of("lkj sdlasoibnnnppap dw"), 5);
    private static final List<String> separatorLines = List.of("Delimiter line #1", "Delimiter line #2 or something");


    void setupJoiner(String searchTerm) {
        pageJoiner = new PageJoiner(
                () -> new AnalyzeAcrossLines(new LiteralReplacer(searchTerm, "new")),
                separatorLines
        );
    }

    void setupJoinerNoDelimiter(String searchTerm) {
        pageJoiner = new PageJoiner(() -> new AnalyzeAcrossLines(new LiteralReplacer(searchTerm, "new")));
    }

    @Test
    void doNothingWhenProcessingSinglePage() {
        var searchTerm = "Line two.";
        setupJoiner(searchTerm);

        pageJoiner.processPage(pageOne);
        FeatureAssert.assertFeaturesHaveCombinedExactlyCoordinates(
                Set.of(new TextCoordinate(pageOne.getPageNumber(), 1, 0).extendToSelection(searchTerm.length())),
                pageJoiner.findFeatures()
        );
    }

    @Test
    void doNothingWhenProcessingSinglePageCrossLine() {
        var searchTerm = "Line two.line";
        setupJoiner(searchTerm);

        pageJoiner.processPage(pageOne);
        FeatureAssert.assertFeaturesHaveCombinedExactlyCoordinates(
                Set.of(new TextSelection(new TextCoordinate(pageOne.getPageNumber(), 1, 0), new TextCoordinate(pageOne.getPageNumber(), 2, 3))),
                pageJoiner.findFeatures()
        );
    }

    @Test
    void correctlyTransformFeaturesFromMiddlePage() {
        var searchTerm = "thatGoes";
        setupJoiner(searchTerm);

        pageJoiner.processPage(pageOne);
        pageJoiner.processPage(pageTwo);
        pageJoiner.processPage(pageThree);
        FeatureAssert.assertFeaturesHaveCombinedExactlyCoordinates(
                Set.of(new TextSelection(new TextCoordinate(pageTwo.getPageNumber(), 0, 13), new TextCoordinate(pageTwo.getPageNumber(), 1, 3))),
                pageJoiner.findFeatures()
        );
    }

    // Note that even though pageTwo (#8) is processed before pageThree (#5), it has a lower page number and is thus treated as the
    // next page after pageOne (#4)
    @Test
    void correctlyTransformFeaturesBetweenPages4and5() {
        var searchTerm = "three" + String.join("", separatorLines) + "lkj";
        setupJoiner(searchTerm);

        pageJoiner.processPage(pageOne);
        pageJoiner.processPage(pageTwo);
        pageJoiner.processPage(pageThree);
        FeatureAssert.assertFeaturesHaveCombinedExactlyCoordinates(
                Set.of(new TextSelection(new TextCoordinate(pageOne.getPageNumber(), 2, 5), new TextCoordinate(pageThree.getPageNumber(), 0, 2))),
                pageJoiner.findFeatures()
        );
    }

    @Test
    void correctlyTransformFeaturesBetweenPages5and8() {
        var searchTerm = "ppap dw" + String.join("", separatorLines) + "Anot";
        setupJoiner(searchTerm);

        pageJoiner.processPage(pageOne);
        pageJoiner.processPage(pageTwo);
        pageJoiner.processPage(pageThree);
        FeatureAssert.assertFeaturesHaveCombinedExactlyCoordinates(
                Set.of(new TextSelection(new TextCoordinate(pageThree.getPageNumber(), 0, 15), new TextCoordinate(pageTwo.getPageNumber(), 0, 3))),
                pageJoiner.findFeatures()
        );
    }

    @Test
    void correctlyTransformFeaturesBetweenPages4and8() {
        var searchTerm = "ree" + String.join("", separatorLines) + "lkj sdlasoibnnnppap dw" + String.join("", separatorLines) + "Anot";
        setupJoiner(searchTerm);

        pageJoiner.processPage(pageOne);
        pageJoiner.processPage(pageTwo);
        pageJoiner.processPage(pageThree);
        FeatureAssert.assertFeaturesHaveCombinedExactlyCoordinates(
                Set.of(new TextSelection(new TextCoordinate(pageOne.getPageNumber(), 2, 7), new TextCoordinate(pageTwo.getPageNumber(), 0, 3))),
                pageJoiner.findFeatures()
        );
    }
}