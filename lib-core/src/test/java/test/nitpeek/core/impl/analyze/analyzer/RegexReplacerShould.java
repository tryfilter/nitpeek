package test.nitpeek.core.impl.analyze.analyzer;

import nitpeek.core.impl.analyze.analyzer.RegexReplacer;
import nitpeek.core.api.analyze.Analyzer;
import nitpeek.core.api.common.Feature;
import nitpeek.core.api.common.FeatureComponent;
import nitpeek.core.api.common.TextCoordinate;
import nitpeek.core.api.common.TextPage;
import nitpeek.core.impl.common.SimpleFeatureComponent;
import nitpeek.core.impl.common.StandardFeature;
import nitpeek.core.impl.process.ListPageConsumer;
import test.nitpeek.core.testutil.TestUtil;
import test.nitpeek.core.testutil.pagesource.HamletAct2ExcerptEdited;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.regex.Pattern;

import static test.nitpeek.core.testutil.FeatureAssert.assertEquivalentFeatures;

final class RegexReplacerShould {

    private Analyzer regexReplacer;
    private static final String NEW = "New Value";
    public static final List<TextPage> pages = new ListPageConsumer(new HamletAct2ExcerptEdited()).getPages();

    private void setupReplacer(String regex, String replacement) {
        regexReplacer = new RegexReplacer(Pattern.compile(regex), replacement);
        for (var page : pages) {
            regexReplacer.processPage(page);
        }
    }

    @Test
    void notFindEmptyString() {
        var replace = "";
        setupReplacer(replace, NEW);
        Assertions.assertTrue(regexReplacer.findFeatures().isEmpty());
    }

    @Test
    void findLiteral() {
        var replace = "contracted";
        setupReplacer(replace, NEW);
        var textLocation = new TextCoordinate(0, 9, 6).extendToSelection(replace.length());
        assertEquivalentFeatures(List.of(replacerFeature(new SimpleFeatureComponent(NEW, textLocation, replace))), regexReplacer);
    }

    @Test
    void findSimpleRegularExpression() {
        var regex = "to the \\w{5}";
        setupReplacer(regex, NEW);
        String expectedMatch1 = "to the heart";
        String expectedMatch2 = "to the mouth";
        var textLocation1 = new TextCoordinate(3, 1, 32).extendToSelection(expectedMatch1.length());
        var textLocation2 = new TextCoordinate(3, 2, 31).extendToSelection(expectedMatch2.length());
        assertEquivalentFeatures(
                List.of(
                        replacerFeature(new SimpleFeatureComponent(NEW, textLocation1, expectedMatch1)),
                        replacerFeature(new SimpleFeatureComponent(NEW, textLocation2, expectedMatch2))
                ),
                regexReplacer
        );
    }

    @Test
    void findRegularExpressionInputMarkerAndCaseInsensitive() {
        var regex = "(?i)^king.*";
        setupReplacer(regex, NEW);
        String expectedMatch1 = "KING CLAUDIUS";
        String expectedMatch2 = expectedMatch1;
        var textLocation1 = new TextCoordinate(0, 4, 0).extendToSelection(expectedMatch1.length());
        var textLocation2 = new TextCoordinate(2, 6, 0).extendToSelection(expectedMatch2.length());
        assertEquivalentFeatures(
                List.of(
                        replacerFeature(new SimpleFeatureComponent(NEW, textLocation1, expectedMatch1)),
                        replacerFeature(new SimpleFeatureComponent(NEW, textLocation2, expectedMatch2))
                ),
                regexReplacer
        );
    }

    @Test
    void findVariableLengthRegularExpression() {
        var regex = "bo(nds|w)";
        setupReplacer(regex, NEW);
        String expectedMatch1 = "bonds";
        String expectedMatch2 = "bow";
        var textLocation1 = new TextCoordinate(1, 8, 29).extendToSelection(expectedMatch1.length());
        var textLocation2 = new TextCoordinate(3, 14, 8).extendToSelection(expectedMatch2.length());
        assertEquivalentFeatures(
                List.of(
                        replacerFeature(new SimpleFeatureComponent(NEW, textLocation1, expectedMatch1)),
                        replacerFeature(new SimpleFeatureComponent(NEW, textLocation2, expectedMatch2))
                ),
                regexReplacer
        );
    }

    @Test
    void findFullyVariableLengthRegularExpression() {
        var regex = "j.*e";
        setupReplacer(regex, NEW);
        String expectedMatch1 = "jointress to this warlike state";
        String expectedMatch2 = "joint and out of frame";
        String expectedMatch3 = "ject: and we here";
        var textLocation1 = new TextCoordinate(0, 14, 13).extendToSelection(expectedMatch1.length());
        var textLocation2 = new TextCoordinate(1, 4, 19).extendToSelection(expectedMatch2.length());
        var textLocation3 = new TextCoordinate(1, 17, 14).extendToSelection(expectedMatch3.length());
        assertEquivalentFeatures(
                List.of(
                        replacerFeature(new SimpleFeatureComponent(NEW, textLocation1, expectedMatch1)),
                        replacerFeature(new SimpleFeatureComponent(NEW, textLocation2, expectedMatch2)),
                        replacerFeature(new SimpleFeatureComponent(NEW, textLocation3, expectedMatch3))
                ),
                regexReplacer
        );
    }

    @Test
    void useCaptureGroupsInReplacement() {
        var regex = "\\b(h|H)is (\\w+)";
        var replacement = "$1er $2";
        setupReplacer(regex, replacement);
        String expectedMatch1 = "his advantage";
        String expectedReplacement1 = "her advantage";
        String expectedMatch2 = "his father";
        String expectedReplacement2 = "her father";
        String expectedMatch3 = "his nephew";
        String expectedReplacement3 = "her nephew";
        String expectedMatch4 = "His further";
        String expectedReplacement4 = "Her further";
        String expectedMatch5 = "his subject";
        String expectedReplacement5 = "her subject";
        var textLocation1 = new TextCoordinate(1, 5, 29).extendToSelection(expectedMatch1.length());
        var textLocation2 = new TextCoordinate(1, 8, 8).extendToSelection(expectedMatch2.length());
        var textLocation3 = new TextCoordinate(1, 14, 8).extendToSelection(expectedMatch3.length());
        var textLocation4 = new TextCoordinate(1, 15, 0).extendToSelection(expectedMatch4.length());
        var textLocation5 = new TextCoordinate(1, 17, 7).extendToSelection(expectedMatch5.length());
        assertEquivalentFeatures(
                List.of(
                        replacerFeature(new SimpleFeatureComponent(expectedReplacement1, textLocation1, expectedMatch1)),
                        replacerFeature(new SimpleFeatureComponent(expectedReplacement2, textLocation2, expectedMatch2)),
                        replacerFeature(new SimpleFeatureComponent(expectedReplacement3, textLocation3, expectedMatch3)),
                        replacerFeature(new SimpleFeatureComponent(expectedReplacement4, textLocation4, expectedMatch4)),
                        replacerFeature(new SimpleFeatureComponent(expectedReplacement5, textLocation5, expectedMatch5))
                ),
                regexReplacer
        );
    }

    @Test
    void preserveUnusedCaptureGroupReferences() {
        var regex = "our (dear|most)? ?(valiant)? brother";
        var replacement = "$2, $1 brother";
        setupReplacer(regex, replacement);
        String expectedMatch1 = "our dear brother";
        String expectedReplacement1 = "$2, dear brother"; // group 2 is unused: keep it as is in the replacement
        String expectedMatch2 = "our most valiant brother";
        String expectedReplacement2 = "valiant, most brother";

        var textLocation1 = new TextCoordinate(0, 6, 21).extendToSelection(expectedMatch1.length());
        var textLocation2 = new TextCoordinate(1, 9, 3).extendToSelection(expectedMatch2.length());

        assertEquivalentFeatures(
                List.of(
                        replacerFeature(new SimpleFeatureComponent(expectedReplacement1, textLocation1, expectedMatch1)),
                        replacerFeature(new SimpleFeatureComponent(expectedReplacement2, textLocation2, expectedMatch2))
                ),
                regexReplacer
        );
    }

    @Test
    void correctlyReplacePrefixSharingCapturingGroups() {
        var regex = "(r)(.)(.)(.)(.)(n)(.)(t)(.)(.)(.)(.)";
        var replacement = "'$12' and '$10'";
        setupReplacer(regex, replacement);
        String expectedMatch1 = "reason to th";
        String expectedReplacement1 = "'h' and ' '";
        String expectedMatch2 = "return to Fr";
        String expectedReplacement2 = "'r' and ' '";
        var textLocation1 = new TextCoordinate(2, 13, 24).extendToSelection(expectedMatch1.length());
        var textLocation2 = new TextCoordinate(3, 9, 29).extendToSelection(expectedMatch2.length());
        assertEquivalentFeatures(
                List.of(
                        replacerFeature(new SimpleFeatureComponent(expectedReplacement1, textLocation1, expectedMatch1)),
                        replacerFeature(new SimpleFeatureComponent(expectedReplacement2, textLocation2, expectedMatch2))
                ),
                regexReplacer
        );
    }


    private Feature replacerFeature(FeatureComponent... components) {
        return TestUtil.featureFromComponents(StandardFeature.REPLACE_REGEX.getType(), components);
    }
}
