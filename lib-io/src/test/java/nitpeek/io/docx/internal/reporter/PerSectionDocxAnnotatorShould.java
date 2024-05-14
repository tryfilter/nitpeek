package nitpeek.io.docx.internal.reporter;

import nitpeek.core.api.process.RuleSetProvider;
import nitpeek.core.api.process.RuleSetTag;
import nitpeek.core.api.process.StandardRuleSetTags;
import nitpeek.core.api.report.ReportingException;
import nitpeek.core.impl.analyze.SimpleRule;
import nitpeek.core.impl.analyze.analyzer.CrossLineCrossPageAnalyzer;
import nitpeek.core.impl.analyze.analyzer.RegexReplacer;
import nitpeek.core.impl.common.SimpleIdentifier;
import nitpeek.core.impl.config.SimpleContext;
import nitpeek.core.impl.process.SimpleRuleSetProvider;
import nitpeek.core.impl.translate.CoreEnglishTranslation;
import nitpeek.io.docx.render.HighlightAnnotationRenderer;
import nitpeek.io.docx.render.PerSectionDocxAnnotator;
import nitpeek.io.docx.render.SimpleDocxAnnotator;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.regex.Pattern;

@Disabled("Time consuming test, that needs to be verified manually. Unfortunately it doesn't even work as a " +
        "regression detector since it modifies the files even if there is no change in behavior")
final class PerSectionDocxAnnotatorShould {

    private static final String REPLACE = "Better Value";

    @Test
    void applySameAnnotationInAllSegments() {
        insertHighlightAnnotationsForSearchTerms("all_t", "t", "t", "t");
    }

    // This should have the same effect as the above test
    @Test
    void applyUniversalAnnotationsInAllSegments() {
        insertHighlightAnnotationsForSearchTerms("universal_t", null, null, "t");
    }

    @Test
    void applyBodyAnnotationsOnlyToBodySegments() {
        insertHighlightAnnotationsForSearchTerms("body_t", "t", null, null);
    }

    @Test
    void applyFootnoteAnnotationsOnlyToFootnoteSegments() {
        insertHighlightAnnotationsForSearchTerms("footnote_t", null, "t", null);
    }

    @Test
    void applyBodyAndFootnoteAnnotationsOnlyToBodyAndFootnoteSegments() {
        insertHighlightAnnotationsForSearchTerms("body_footnote_t", "t", "t", null);
    }

    @Test
    void applySeparateAnnotationsForEachSegmentCategory() {
        insertHighlightAnnotationsForSearchTerms("segmented_body-e_footnotes-t_universal-i", "e", "t", "i");
    }


    private void insertHighlightAnnotationsForSearchTerms(String filename, String bodySearchTerm, String footnotesSearchTerm, String universalSearchTerm) {

        try (var input = SimpleDocxAnnotatorShould.class.getResourceAsStream("../../TestFile.docx");
             var output = Files.newOutputStream(Path.of("src", "test", "resources", "temp", "combo_highlight_#" + filename + "_TestFile.docx"))) {
            var docxToAnnotate = WordprocessingMLPackage.load(input);
            var rulesBody = getRuleSetProvider(bodySearchTerm, Set.of(StandardRuleSetTags.contentBody()));
            var rulesFootnotes = getRuleSetProvider(footnotesSearchTerm, Set.of(StandardRuleSetTags.contentFootnotes()));
            var rulesUniversal = getRuleSetProvider(universalSearchTerm, Set.of(StandardRuleSetTags.contentAny()));
            var annotator = new PerSectionDocxAnnotator(Set.of(rulesBody, rulesFootnotes, rulesUniversal), new CoreEnglishTranslation(), SimpleDocxAnnotator::defaultExtractorFactory, new SimpleContext());
            annotator.annotateDocument(docxToAnnotate, new HighlightAnnotationRenderer(HighlightAnnotationRenderer.HighlightColor.CYAN));
            docxToAnnotate.save(output);
        } catch (IOException | ReportingException | Docx4JException e) {
            Assertions.fail("Exception thrown during annotation insertion", e);
        }
    }

    private static RuleSetProvider getRuleSetProvider(String toReplace, Set<RuleSetTag> tags) {
        var ruleId = new SimpleIdentifier("test", "Test", "testing");
        var ruleSetId = new SimpleIdentifier("test", "Test", "testing");
        return new SimpleRuleSetProvider(
                toReplace == null ? Set.of() : Set.of(
                        new SimpleRule(
                                () -> ruleId,
                                () -> new CrossLineCrossPageAnalyzer(new RegexReplacer(Pattern.compile(toReplace), REPLACE))
                        )
                ),
                ruleSetId,
                tags
        );
    }
}