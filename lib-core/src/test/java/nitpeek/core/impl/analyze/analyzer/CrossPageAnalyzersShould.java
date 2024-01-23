package nitpeek.core.impl.analyze.analyzer;

import nitpeek.core.api.analyze.Analyzer;
import nitpeek.core.impl.analyze.SimpleTextPage;
import nitpeek.core.api.common.TextPage;
import nitpeek.core.api.common.TextCoordinate;
import nitpeek.core.api.common.TextSelection;
import nitpeek.core.testutil.FeatureAssert;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

interface CrossPageAnalyzersShould {

    TextPage pageOne = new SimpleTextPage(List.of(
            "Line one.",
            "Line two.",
            "line three"
    ), 4);
    TextPage pageTwo = new SimpleTextPage(List.of(
            "Another line that",
            "Goes over ",
            "multiple lines..."
    ), 8);
    TextPage pageThree = new SimpleTextPage(List.of("lkj sdlasoibnnnppap dw"), 5);


    Analyzer setupPageJoiner(String searchTerm);

    List<String> getSeparatorLines();


    @Test
    default void doNothingWhenProcessingSinglePage() {
        var searchTerm = "Line two.";
        Analyzer pageJoiner = setupPageJoiner(searchTerm);

        pageJoiner.processPage(pageOne);
        FeatureAssert.assertFeaturesHaveCombinedExactlyCoordinates(
                Set.of(new TextCoordinate(pageOne.getPageNumber(), 1, 0).extendToSelection(searchTerm.length())),
                pageJoiner.findFeatures()
        );
    }

    @Test
    default void doNothingWhenProcessingSinglePageCrossLine() {
        var searchTerm = "Line two.line";
        Analyzer pageJoiner = setupPageJoiner(searchTerm);

        pageJoiner.processPage(pageOne);
        FeatureAssert.assertFeaturesHaveCombinedExactlyCoordinates(
                Set.of(new TextSelection(new TextCoordinate(pageOne.getPageNumber(), 1, 0), new TextCoordinate(pageOne.getPageNumber(), 2, 3))),
                pageJoiner.findFeatures()
        );
    }

    @Test
    default void correctlyTransformFeaturesFromMiddlePage() {
        var searchTerm = "thatGoes";
        Analyzer pageJoiner = setupPageJoiner(searchTerm);

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
    default void correctlyTransformFeaturesBetweenPages4and5() {
        var searchTerm = joinUsingSeparators("three", "lkj");
        Analyzer pageJoiner = setupPageJoiner(searchTerm);

        pageJoiner.processPage(pageOne);
        pageJoiner.processPage(pageTwo);
        pageJoiner.processPage(pageThree);
        FeatureAssert.assertFeaturesHaveCombinedExactlyCoordinates(
                Set.of(new TextSelection(new TextCoordinate(pageOne.getPageNumber(), 2, 5), new TextCoordinate(pageThree.getPageNumber(), 0, 2))),
                pageJoiner.findFeatures()
        );
    }

    @Test
    default void correctlyTransformFeaturesBetweenPages5and8() {
        var searchTerm = joinUsingSeparators("ppap dw", "Anot");
        Analyzer pageJoiner = setupPageJoiner(searchTerm);

        pageJoiner.processPage(pageOne);
        pageJoiner.processPage(pageTwo);
        pageJoiner.processPage(pageThree);
        FeatureAssert.assertFeaturesHaveCombinedExactlyCoordinates(
                Set.of(new TextSelection(new TextCoordinate(pageThree.getPageNumber(), 0, 15), new TextCoordinate(pageTwo.getPageNumber(), 0, 3))),
                pageJoiner.findFeatures()
        );
    }

    @Test
    default void correctlyTransformFeaturesBetweenPages4and8() {
        var searchTerm = joinUsingSeparators("ree", "lkj sdlasoibnnnppap dw", "Anot");
        Analyzer pageJoiner = setupPageJoiner(searchTerm);

        pageJoiner.processPage(pageOne);
        pageJoiner.processPage(pageTwo);
        pageJoiner.processPage(pageThree);
        FeatureAssert.assertFeaturesHaveCombinedExactlyCoordinates(
                Set.of(new TextSelection(new TextCoordinate(pageOne.getPageNumber(), 2, 7), new TextCoordinate(pageTwo.getPageNumber(), 0, 3))),
                pageJoiner.findFeatures()
        );
    }

    default String joinUsingSeparators(String... searchTerms) {
        return String.join(String.join("", getSeparatorLines()), searchTerms);
    }
}