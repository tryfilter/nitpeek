package nitpeek.io.docx;

import jakarta.xml.bind.JAXBException;
import nitpeek.core.api.process.RuleSetProvider;
import nitpeek.core.api.report.ReportingException;
import nitpeek.core.impl.analyze.SimpleRule;
import nitpeek.core.impl.analyze.analyzer.CrossLineCrossPageAnalyzer;
import nitpeek.core.impl.analyze.analyzer.RegexReplacer;
import nitpeek.core.impl.common.SimpleIdentifier;
import nitpeek.core.impl.process.SimpleProcessor;
import nitpeek.core.impl.process.SimpleRuleSetProvider;
import nitpeek.core.impl.translate.DefaultFallbackEnglishTranslation;
import nitpeek.io.docx.render.HighlightAnnotationRenderer;
import org.docx4j.openpackaging.exceptions.Docx4JException;
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

import static nitpeek.io.docx.render.HighlightAnnotationRenderer.*;

@Disabled("Time consuming test, that needs to be verified manually. Unfortunately it doesn't even work as a" +
        "regression detector since it modifies the files even if there is no change in behavior")
final class DocxReporterShould {
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

    // Common issue: any selections in one header/footer are duplicated to the header/footer of all pages.
    @Test
    void insertAnnotationFirstWord() {
        insertHighlightAnnotationsForSearchTerm("HEADER", "#FirstWord");
    }

    @Test
    void insertAnnotationFullHeading() {
        insertHighlightAnnotationsForSearchTerm("Main Heading", "#Heading");
    }

    // Common issue: any selections in one header/footer are duplicated to the header/footer of all pages.
    @Test
    void insertAnnotationSingleLetter() {
        insertHighlightAnnotationsForSearchTerm("g", "#SingleLetter");
    }

    @Test
    void insertAnnotationSingleBacktick() {
        insertHighlightAnnotationsForSearchTerm("`", "#Backtick");
    }

    @Test
    void insertAnnotationSpecialToSpecialTwoLines() {
        insertHighlightAnnotationsForSearchTerm("ß.*“”", "#Special");
    }

    @Test
    void insertAnnotationTallerMiddle() {
        insertHighlightAnnotationsForSearchTerm("simple", "#TallerMiddle");
    }

    @Test
    void insertAnnotationNotOnWordBoundaries() {
        insertHighlightAnnotationsForSearchTerm("me simple te", "#CrossWordBoundary");
    }

    // Fails to highlight the 2 in "Page 2/3" on the footer of the second page
    // This appears to be a limitation of Word.
    @Test
    void insertAnnotationDigit() {
        insertHighlightAnnotationsForSearchTerm("2", "#Digit");
    }

    @Test
    void insertAnnotationOtherFontSection() {
        insertHighlightAnnotationsForSearchTerm("ferent.*hyphen", "#OtherFontSection");
    }

    @Test
    void insertAnnotationTwoLine() {
        insertHighlightAnnotationsForSearchTerm("three.*ending", "#TwoLine");
    }

    @Test
    void insertAnnotationMultiLine() {
        insertHighlightAnnotationsForSearchTerm("three.*Special", "#MultiLine");
    }

    // Common issue: any selections in one header/footer are duplicated to the header/footer of all pages.
    @Test
    void insertAnnotationMultiPage() {
        insertHighlightAnnotationsForSearchTerm("third.*three", "#MultiPage");
    }


    private void insertHighlightAnnotationsForSearchTerm(String searchTerm, String filename) {
        var processor = getReplacingCrossPageProcessor(searchTerm);

        try (var input = DocxReporterShould.class.getResourceAsStream("TestFile.docx");
             var output = Files.newOutputStream(Path.of("src", "test", "resources", "temp", "highlight_" + filename + "_TestFile.docx"))) {
            var inMemory = new ByteArrayOutputStream();
            input.transferTo(inMemory);
            var docxToAnalyze = new ByteArrayInputStream(inMemory.toByteArray());
            var docxToAnnotate = new ByteArrayInputStream(inMemory.toByteArray());
            var docxSource = DocxPageSource.createFrom(docxToAnalyze);
            processor.startProcessing(docxSource);
            var features = processor.getFeatures();

            var reporter = new DocxReporter(docxToAnnotate, output, new HighlightAnnotationRenderer(HighlightColor.CYAN));
            reporter.reportFeatures(features, new DefaultFallbackEnglishTranslation());
        } catch (IOException | ReportingException | JAXBException | Docx4JException e) {
            Assertions.fail("Exception thrown during annotation insertion", e);
        }
    }

    private static SimpleProcessor getReplacingCrossPageProcessor(String toReplace) {
        return new SimpleProcessor(getRuleSetProvider(toReplace, REPLACE));
    }
}