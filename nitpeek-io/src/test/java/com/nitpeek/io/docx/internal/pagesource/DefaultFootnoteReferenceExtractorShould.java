package com.nitpeek.io.docx.internal.pagesource;

import com.nitpeek.core.api.common.TextCoordinate;
import com.nitpeek.core.api.config.standard.footnote.FootnoteReference;
import com.nitpeek.core.api.report.ReportingException;
import com.nitpeek.io.docx.internal.common.RunRendererFactory;
import com.nitpeek.io.docx.internal.pagesource.render.SimpleArabicNumberRenderer;
import com.nitpeek.io.docx.internal.pagesource.render.SimpleRunRenderer;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Set;
import java.util.function.UnaryOperator;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class DefaultFootnoteReferenceExtractorShould {
    @Test
    void extractFootnoteReferences() throws IOException, Docx4JException, ReportingException {

        try (var input = DefaultFootnoteReferenceExtractorShould.class.getResourceAsStream("../../TestFile.docx")) {

            var docxToAnnotate = WordprocessingMLPackage.load(input);
            var pages = new ParagraphPreservingDocxPageExtractor(docxToAnnotate, UnaryOperator.identity()).extractPages();

            RunRendererFactory runRendererFactory = (int pageIndex, int pageCount) -> new SimpleRunRenderer(pageIndex, pageCount, new SimpleArabicNumberRenderer());
            var defaultFootnoteReferenceExtractor = new DefaultFootnoteReferenceExtractor(runRendererFactory, pages.size());
            var footnoteReferencesP1 = defaultFootnoteReferenceExtractor.extractFootnoteReferences(pages.get(0), 0);
            var footnoteReferencesP2 = defaultFootnoteReferenceExtractor.extractFootnoteReferences(pages.get(1), 1);
            var footnoteReferencesP3 = defaultFootnoteReferenceExtractor.extractFootnoteReferences(pages.get(2), 2);

            // only the first page has footnotes
            assertEquals(Set.of(), footnoteReferencesP2);
            assertEquals(Set.of(), footnoteReferencesP3);

            // Note that the line numbers ignore the header line
            var expected = Set.of(
                    new FootnoteReference(1, new TextCoordinate(0, 9, 9).extendToSelection(1)),
                    new FootnoteReference(2, new TextCoordinate(0, 9, 29).extendToSelection(1)),
                    new FootnoteReference(3, new TextCoordinate(0, 10, 20).extendToSelection(1))
            );
            assertEquals(expected, footnoteReferencesP1);
        }
    }
}
