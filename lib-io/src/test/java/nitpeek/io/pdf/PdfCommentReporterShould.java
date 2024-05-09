package nitpeek.io.pdf;

import nitpeek.core.api.process.RuleSetProvider;
import nitpeek.core.api.report.ReportingException;
import nitpeek.core.impl.analyze.SimpleRule;
import nitpeek.core.impl.analyze.analyzer.CrossLineCrossPageAnalyzer;
import nitpeek.core.impl.analyze.analyzer.RegexReplacer;
import nitpeek.core.impl.common.SimpleIdentifier;
import nitpeek.core.impl.process.RulesBasedPageConsumer;
import nitpeek.core.impl.process.SimplePageProcessor;
import nitpeek.core.impl.process.SimpleProcessor;
import nitpeek.core.impl.process.SimpleRuleSetProvider;
import nitpeek.core.impl.translate.DefaultFallbackEnglishTranslation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.regex.Pattern;

@Disabled("These tests are slow to run and their results must be verified manually (visual check)")
final class PdfCommentReporterShould {

    private static final String REPLACE = "Better Value";

    private static RuleSetProvider getRuleSetProvider(String toReplace, String replaceWith) {
        var ruleId = new SimpleIdentifier("test", "Test", "testing");
        var ruleSetId = new SimpleIdentifier("test", "Test", "testing");
        return new SimpleRuleSetProvider(
                Set.of(new SimpleRule(
                        () -> ruleId,
                        () -> new CrossLineCrossPageAnalyzer(new RegexReplacer(Pattern.compile(toReplace), replaceWith))
                )),
                ruleSetId);
    }

    @Test
    void insertAnnotationFirstWord() {
        insertAnnotationsForSearchTerm("HEADER", "#FirstWord");
    }

    @Test
    void insertAnnotationFullHeading() {
        insertAnnotationsForSearchTerm("Main Heading", "#Heading");
    }

    @Test
    void insertAnnotationSingleLetter() {
        insertAnnotationsForSearchTerm("g", "#SingleLetter");
    }

    @Test
    void insertAnnotationSingleBacktick() {
        insertAnnotationsForSearchTerm("`", "#Backtick");
    }

    @Test
    void insertAnnotationSpecialToSpecialTwoLines() {
        insertAnnotationsForSearchTerm("ß.*“”", "#Special");
    }

    @Test
    void insertAnnotationTallerMiddle() {
        insertAnnotationsForSearchTerm("simple", "#TallerMiddle");
    }

    @Test
    void insertAnnotationNotOnWordBoundaries() {
        insertAnnotationsForSearchTerm("me simple te", "#CrossWordBoundary");
    }

    @Test
    void insertAnnotationDigit() {
        insertAnnotationsForSearchTerm("2", "#Digit");
    }

    @Test
    void insertAnnotationOtherFontSection() {
        insertAnnotationsForSearchTerm("ferent.*hyphen", "#OtherFontSection");
    }

    @Test
    void insertAnnotationTwoLine() {
        insertAnnotationsForSearchTerm("three.*ending", "#TwoLine");
    }

    @Test
    void insertAnnotationMultiLine() {
        insertAnnotationsForSearchTerm("three.*Special", "#MultiLine");
    }

    @Test
    void insertAnnotationMultiPage() {
        insertAnnotationsForSearchTerm("third.*three", "#MultiPage");
    }


    private void insertAnnotationsForSearchTerm(String searchTerm, String filename) {
        var processor = getReplacingCrossPageProcessor(searchTerm);

        try (var input = PdfCommentReporterShould.class.getResourceAsStream("TestFile.pdf");
             var output = Files.newOutputStream(Path.of("src", "test", "resources", "temp", "highlight_" + filename + "_TestFile.pdf"))) {
            var inMemory = new ByteArrayOutputStream();
            input.transferTo(inMemory);
            var pdfToAnalyze = new ByteArrayInputStream(inMemory.toByteArray());
            var pdfToAnnotate = new ByteArrayInputStream(inMemory.toByteArray());
            var pdfSource = PdfPageSource.createFrom(pdfToAnalyze);
            processor.startProcessing(pdfSource);
            var features = processor.getFeatures();

            var reporter = new PdfCommentReporter(pdfToAnnotate, output);
            reporter.reportFeatures(features, new DefaultFallbackEnglishTranslation());
        } catch (IOException | ReportingException e) {
            Assertions.fail("Exception thrown during annotation insertion", e);
        }
    }

    private static SimpleProcessor getReplacingCrossPageProcessor(String toReplace) {
        return new SimpleProcessor(new RulesBasedPageConsumer(getRuleSetProvider(toReplace, PdfCommentReporterShould.REPLACE).getRules(), new SimplePageProcessor()));
    }
}