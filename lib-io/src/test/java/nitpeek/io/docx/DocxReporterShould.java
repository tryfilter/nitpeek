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
import nitpeek.io.docx.render.CompositeRun;
import nitpeek.io.docx.render.DocxPage;
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
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static nitpeek.io.docx.render.HighlightAnnotationRenderer.HighlightColor;

@Disabled("Time consuming test, that needs to be verified manually. Unfortunately it doesn't even work as a " +
        "regression detector since it modifies the files even if there is no change in behavior")
final class DocxReporterShould {
    private static final String REPLACE = "Better Value";

    private static RuleSetProvider getRuleSetProvider(List<String> toReplace, String replaceWith) {
        var ruleId = new SimpleIdentifier("test", "Test", "testing");
        var ruleSetId = new SimpleIdentifier("test", "Test", "testing");
        return new SimpleRuleSetProvider(
                toReplace.stream().map(replace -> new SimpleRule(
                        () -> ruleId,
                        () -> new CrossLineCrossPageAnalyzer(new RegexReplacer(Pattern.compile(replace), replaceWith))
                )).collect(Collectors.toUnmodifiableSet()),
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

    // Common issue: any selections in one header/footer are duplicated to the header/footer of all pages.
    @Test
    void insertAnnotationDigit() {
        insertHighlightAnnotationsForSearchTerm("2", "#Digit");
    }

    @Test
    void insertAnnotationDigitInFootnotesOnly() {
        insertHighlightAnnotationsForSearchTerm("2", "#Footnotes_Digit", PageTransformers::keepOnlyFootnotes);
    }

    @Test
    void insertAnnotationLetterInFootnotesOnly() {
        insertHighlightAnnotationsForSearchTerm("o", "#Footnotes_o", PageTransformers::keepOnlyFootnotes);
    }

    @Test
    void insertAnnotationDigitInBodyOnly() {
        insertHighlightAnnotationsForSearchTerm("2", "#Body_Digit", PageTransformers::keepOnlyBody);
    }

    @Test
    void insertAnnotationLetterInBodyOnly() {
        insertHighlightAnnotationsForSearchTerm("o", "#Body_o", PageTransformers::keepOnlyBody);
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

    @Test
    void insertMultipleAnnotationsInSameRun() {
        insertHighlightAnnotationsForSearchTerm(List.of("g", "le-", "Sin", "Pa", "ine"), "#MultiAnnotation");
    }

    private void insertHighlightAnnotationsForSearchTerm(String searchTerm, String filename) {
        insertHighlightAnnotationsForSearchTerm(List.of(searchTerm), filename);
    }

    private void insertHighlightAnnotationsForSearchTerm(String searchTerm, String filename, UnaryOperator<DocxPage<CompositeRun>> pageTransformer) {
        insertHighlightAnnotationsForSearchTerm(List.of(searchTerm), filename, pageTransformer);
    }

    private void insertHighlightAnnotationsForSearchTerm(List<String> searchTerms, String filename) {
        insertHighlightAnnotationsForSearchTerm(searchTerms, filename, UnaryOperator.identity());
    }

    private void insertHighlightAnnotationsForSearchTerm(List<String> searchTerms, String filename, UnaryOperator<DocxPage<CompositeRun>> pageTransformer) {
        var processor = getReplacingCrossPageProcessor(searchTerms);

        try (var input = DocxReporterShould.class.getResourceAsStream("TestFile.docx");
             var output = Files.newOutputStream(Path.of("src", "test", "resources", "temp", "highlight_" + filename + "_TestFile.docx"))) {
            var inMemory = new ByteArrayOutputStream();
            input.transferTo(inMemory);
            var docxToAnalyze = new ByteArrayInputStream(inMemory.toByteArray());
            var docxToAnnotate = new ByteArrayInputStream(inMemory.toByteArray());
            var docxSource = DocxPageSource.createFrom(docxToAnalyze, pageTransformer);
            processor.startProcessing(docxSource);
            var features = processor.getFeatures();

            var reporter = new DocxReporter(docxToAnnotate, output, new HighlightAnnotationRenderer(HighlightColor.CYAN), pageTransformer);
            reporter.reportFeatures(features, new DefaultFallbackEnglishTranslation());
        } catch (IOException | ReportingException | JAXBException | Docx4JException e) {
            Assertions.fail("Exception thrown during annotation insertion", e);
        }
    }

    private static SimpleProcessor getReplacingCrossPageProcessor(List<String> replacementTerms) {
        return new SimpleProcessor(getRuleSetProvider(replacementTerms, REPLACE));
    }
}