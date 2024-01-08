package nitpeek.core.api.analyze.transformer;

import nitpeek.core.api.analyze.SimpleTextPage;
import nitpeek.core.api.analyze.analyzer.Analyzer;
import nitpeek.core.api.analyze.analyzer.LiteralReplacer;
import nitpeek.core.api.analyze.analyzer.TransformingAnalyzer;
import nitpeek.core.api.common.TextCoordinate;
import nitpeek.core.api.common.TextSelection;
import nitpeek.core.testutil.FeatureAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

final class HyphenJoinerShould {

    private static final String searchTerm = "searchterm";
    private Transformer hyphenJoiner;
    private Analyzer literalReplacer;

    @BeforeEach
    void setup() {
        hyphenJoiner = new HyphenJoiner();
        literalReplacer = new TransformingAnalyzer(new LiteralReplacer(searchTerm, "new"), hyphenJoiner);
    }

    @Test
    void doNothingToHyphensNotAtTheEndOfLine() {
        var page = new SimpleTextPage(List.of(
                "Simple line containing search-term hyphenated search",
                "-term, searchter-m "
        ), 0);

        literalReplacer.processPage(page);

        FeatureAssert.assertFeaturesHaveCombinedExactlyCoordinates(Set.of(), literalReplacer.findFeatures());
    }

    @Test
    void joinHyphenatedWords() {
        var page = new SimpleTextPage(List.of(
                "Simple line containing the term for hyphenated search-",
                "term, s-",
                "earchterm, blabla s searchter-",
                "m se-",
                "archterm"
        ), 0);

        literalReplacer.processPage(page);
        var expectedCoordinates = Set.of(
                new TextSelection(new TextCoordinate(page.getPageNumber(), 0, 47), new TextCoordinate(page.getPageNumber(), 1, 3)),
                new TextSelection(new TextCoordinate(page.getPageNumber(), 1, 6), new TextCoordinate(page.getPageNumber(), 2, 8)),
                new TextSelection(new TextCoordinate(page.getPageNumber(), 2, 20), new TextCoordinate(page.getPageNumber(), 3, 0)),
                new TextSelection(new TextCoordinate(page.getPageNumber(), 3, 2), new TextCoordinate(page.getPageNumber(), 4, 7))
        );

        FeatureAssert.assertFeaturesHaveCombinedExactlyCoordinates(expectedCoordinates, literalReplacer.findFeatures());
    }

    @Test
    void considerHyphenSymbolPartOfWordsWithDefaultSettings() {
        var page = new SimpleTextPage(List.of(
                "search-",
                "te-rm, sabatical"
        ), 0);

        Analyzer literalReplacerCustom = new TransformingAnalyzer(new LiteralReplacer("searchte-rm", "new"), hyphenJoiner);

        literalReplacerCustom.processPage(page);
        var expectedCoordinates = Set.of(
                new TextSelection(new TextCoordinate(page.getPageNumber(), 0, 0), new TextCoordinate(page.getPageNumber(), 1, 4))
        );

        FeatureAssert.assertFeaturesHaveCombinedExactlyCoordinates(expectedCoordinates, literalReplacerCustom.findFeatures());
    }

    @Test
    void workWithLineEntirelyMadeOfContinuation() {
        var page = new SimpleTextPage(List.of(
                "hyphenated search-",
                "term",
                "earchterm, blabla s searchter-"
        ), 0);

        literalReplacer.processPage(page);
        var expectedCoordinates = Set.of(
                new TextSelection(new TextCoordinate(page.getPageNumber(), 0, 11), new TextCoordinate(page.getPageNumber(), 1, 3))
        );

        FeatureAssert.assertFeaturesHaveCombinedExactlyCoordinates(expectedCoordinates, literalReplacer.findFeatures());
    }

    @Test
    void joinMultiLineHyphenation() {
        var page = new SimpleTextPage(List.of(
                "abc se-",
                "arc-",
                "h-",
                "term"
        ), 0);

        literalReplacer.processPage(page);
        var expectedCoordinates = Set.of(
                new TextSelection(new TextCoordinate(page.getPageNumber(), 0, 4), new TextCoordinate(page.getPageNumber(), 3, 3))
        );

        FeatureAssert.assertFeaturesHaveCombinedExactlyCoordinates(expectedCoordinates, literalReplacer.findFeatures());
    }

    @Test
    void joinMultiLineHyphenationSplit() {
        var page = new SimpleTextPage(List.of(
                "abc se-",
                "archterm",
                "search-",
                "term"
        ), 0);


        literalReplacer.processPage(page);
        var expectedCoordinates = Set.of(
                new TextSelection(new TextCoordinate(page.getPageNumber(), 0, 4), new TextCoordinate(page.getPageNumber(), 1, 7)),
                new TextSelection(new TextCoordinate(page.getPageNumber(), 2, 0), new TextCoordinate(page.getPageNumber(), 3, 3))
        );

        FeatureAssert.assertFeaturesHaveCombinedExactlyCoordinates(expectedCoordinates, literalReplacer.findFeatures());
    }

    @Test
    void joinMultiLineHyphenationOnlyHyphen() {
        var page = new SimpleTextPage(List.of(
                "abc se-",
                "arc-",
                "-",
                "hterm"
        ), 0);

        literalReplacer.processPage(page);
        var expectedCoordinates = Set.of(
                new TextSelection(new TextCoordinate(page.getPageNumber(), 0, 4), new TextCoordinate(page.getPageNumber(), 3, 4))
        );

        FeatureAssert.assertFeaturesHaveCombinedExactlyCoordinates(expectedCoordinates, literalReplacer.findFeatures());
    }

    @Test
    void notProduceEmptyLines() {
        var page = new SimpleTextPage(List.of(
                "abc se-",
                "arch",
                "search-",
                "term"
        ), 0);

        var expected = List.of(
                "abc search",
                "searchterm"
        );

        Assertions.assertEquals(expected, hyphenJoiner.transformPage(page).getLines());
    }

    @Test
    void joinMultipleMultiLines() {
        var page = new SimpleTextPage(List.of(
                "abcde-",
                "fghi-",
                "jklm-",
                "no",
                "p-",
                "q-",
                "rst-",
                "uvw",
                "x",
                "y-",
                "z"
        ), 0);

        var expected = List.of(
                "abcdefghijklmno",
                "pqrstuvw",
                "x",
                "yz"
        );

        Assertions.assertEquals(expected, hyphenJoiner.transformPage(page).getLines());
    }

}