package nitpeek.io.docx.internal.reporter;

import nitpeek.core.api.process.RuleSetProvider;
import nitpeek.core.api.report.ReportingException;
import nitpeek.core.impl.analyze.SimpleRule;
import nitpeek.core.impl.analyze.analyzer.CrossLineCrossPageAnalyzer;
import nitpeek.core.impl.analyze.analyzer.RegexReplacer;
import nitpeek.core.impl.common.SimpleIdentifier;
import nitpeek.core.impl.process.SimpleRuleSetProvider;
import nitpeek.core.impl.translate.DefaultFallbackEnglishTranslation;
import nitpeek.io.docx.render.SimpleDocxAnnotator;
import nitpeek.io.docx.types.DocxPage;
import nitpeek.io.docx.types.CompositeRun;
import nitpeek.io.docx.render.HighlightAnnotationRenderer;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

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
final class SimpleDocxAnnotatorShould {
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
        insertHighlightAnnotationsForSearchTerm("HEADER", "FirstWord");
    }

    @Test
    void insertAnnotationFullHeading() {
        insertHighlightAnnotationsForSearchTerm("Main Heading", "Heading");
    }

    // Common issue: any selections in one header/footer are duplicated to the header/footer of all pages.
    @Test
    void insertAnnotationSingleLetter() {
        insertHighlightAnnotationsForSearchTerm("g", "SingleLetter");
    }

    @Test
    void insertAnnotationSingleBacktick() {
        insertHighlightAnnotationsForSearchTerm("`", "Backtick");
    }

    @Test
    void insertAnnotationSpecialToSpecialTwoLines() {
        insertHighlightAnnotationsForSearchTerm("ß.*“”", "Special");
    }

    @Test
    void insertAnnotationTallerMiddle() {
        insertHighlightAnnotationsForSearchTerm("simple", "TallerMiddle");
    }

    @Test
    void insertAnnotationNotOnWordBoundaries() {
        insertHighlightAnnotationsForSearchTerm("me simple te", "CrossWordBoundary");
    }

    // Common issue: any selections in one header/footer are duplicated to the header/footer of all pages.
    @Test
    void insertAnnotationDigit() {
        insertHighlightAnnotationsForSearchTerm("2", "Digit");
    }

    @Test
    void insertAnnotationDigitInFootnotesOnly() {
        insertHighlightAnnotationsForSearchTerm("2", "Footnotes_Digit", PageTransformers::keepOnlyFootnotes);
    }

    @Test
    void insertAnnotationLetterInFootnotesOnly() {
        insertHighlightAnnotationsForSearchTerm("o", "Footnotes_o", PageTransformers::keepOnlyFootnotes);
    }

    @Test
    void insertAnnotationDigitInBodyOnly() {
        insertHighlightAnnotationsForSearchTerm("2", "Body_Digit", PageTransformers::keepOnlyBody);
    }

    @Test
    void insertAnnotationLetterInBodyOnly() {
        insertHighlightAnnotationsForSearchTerm("o", "Body_o", PageTransformers::keepOnlyBody);
    }

    @Test
    void insertAnnotationOtherFontSection() {
        insertHighlightAnnotationsForSearchTerm("ferent.*hyphen", "OtherFontSection");
    }

    @Test
    void insertAnnotationTwoLine() {
        insertHighlightAnnotationsForSearchTerm("three.*ending", "TwoLine");
    }

    @Test
    void insertAnnotationMultiLine() {
        insertHighlightAnnotationsForSearchTerm("three.*Special", "MultiLine");
    }

    // Common issue: any selections in one header/footer are duplicated to the header/footer of all pages.
    @Test
    void insertAnnotationMultiPage() {
        insertHighlightAnnotationsForSearchTerm("third.*three", "MultiPage");
    }

    @Test
    void insertMultipleAnnotationsInSameRun() {
        insertHighlightAnnotationsForSearchTerm(List.of("g", "le-", "Sin", "Pa", "ine"), "MultiAnnotation");
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

        try (var input = SimpleDocxAnnotatorShould.class.getResourceAsStream("../../TestFile.docx");
             var output = Files.newOutputStream(Path.of("src", "test", "resources", "temp", "highlight_#" + filename + "_TestFile.docx"))) {
            var docxToAnnotate = WordprocessingMLPackage.load(input);
            var annotator = new SimpleDocxAnnotator(getRuleSetProvider(searchTerms, REPLACE), new DefaultFallbackEnglishTranslation(), pageTransformer, SimpleDocxAnnotator::defaultExtractorFactory);
            annotator.annotateDocument(docxToAnnotate, new HighlightAnnotationRenderer(HighlightColor.CYAN));
            docxToAnnotate.save(output);
        } catch (IOException | ReportingException | Docx4JException e) {
            Assertions.fail("Exception thrown during annotation insertion", e);
        }
    }
}