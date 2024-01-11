package nitpeek.core.api.analyze.analyzer;

import nitpeek.core.api.analyze.SimpleTextPage;
import nitpeek.core.api.analyze.TextPage;
import nitpeek.core.api.common.TextCoordinate;
import nitpeek.core.api.common.TextSelection;
import nitpeek.core.testutil.FeatureAssert;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

final class CrossLineAnalyzerWrappingLiteralReplacerShould {

    @Test
    void findLiteralsWithinAndAcrossLines() {
        String searchTerm = "replace-this";
        Analyzer crossLineLiteralReplacer = new CrossLineAnalyzer(new LiteralReplacer(searchTerm, "with-this"));
        TextPage page = new SimpleTextPage(List.of(
                "Nothing in this line",
                "replace-this",
                "Mixed in between other woreplace-thisrds",
                "Crossing into next line: repl",
                "ace-this. Now on the dash boundary (not that it should matter) replace-",
                "thisreplacethisreplace-this replace-thi",
                "s",
                "replace-thisreplace-thisreplace-this"
        ), 0);

        Set<TextSelection> expectedFinds = Set.of(
                new TextCoordinate(0, 1, 0).extendToSelection(searchTerm.length()),
                new TextCoordinate(0, 2, 25).extendToSelection(searchTerm.length()),
                new TextSelection(new TextCoordinate(0, 3, 25), new TextCoordinate(0, 4, 7)),
                new TextSelection(new TextCoordinate(0, 4, 63), new TextCoordinate(0, 5, 3)),
                new TextCoordinate(0, 5, 15).extendToSelection(searchTerm.length()),
                new TextSelection(new TextCoordinate(0, 5, 28), new TextCoordinate(0, 6, 0)),
                new TextCoordinate(0, 7, 0).extendToSelection(searchTerm.length()),
                new TextCoordinate(0, 7, searchTerm.length()).extendToSelection(searchTerm.length()),
                new TextCoordinate(0, 7, 2 * searchTerm.length()).extendToSelection(searchTerm.length())
        );

        crossLineLiteralReplacer.processPage(page);

        FeatureAssert.assertFeaturesHaveCombinedExactlyCoordinates(expectedFinds, crossLineLiteralReplacer.findFeatures());

    }
}
