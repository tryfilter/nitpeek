package com.nitpeek.io.docx.internal.pagesource;

import com.nitpeek.core.api.common.TextCoordinate;
import com.nitpeek.core.api.config.standard.footnote.FootnoteContent;
import com.nitpeek.core.api.report.ReportingException;
import com.nitpeek.io.docx.internal.common.RunRendererFactory;
import com.nitpeek.io.docx.internal.pagesource.render.SimpleArabicNumberRenderer;
import com.nitpeek.io.docx.internal.pagesource.render.SimpleRunRenderer;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class DefaultFootnoteContentExtractorShould {
    @Test
    void extractFootnoteReferences() throws IOException, Docx4JException, ReportingException {

        try (var input = DefaultFootnoteContentExtractorShould.class.getResourceAsStream("../../TestFile.docx")) {

            var docxToAnnotate = WordprocessingMLPackage.load(input);
            var pages = new ParagraphPreservingDocxPageExtractor(docxToAnnotate, UnaryOperator.identity()).extractPages();

            RunRendererFactory runRendererFactory = (int pageIndex, int pageCount) -> new SimpleRunRenderer(pageIndex, pageCount, new SimpleArabicNumberRenderer());
            var defaultFootnoteContentExtractor = new DefaultFootnoteContentExtractor(runRendererFactory, pages.size());
            var footnoteReferencesP1 = defaultFootnoteContentExtractor.extractFootnoteContents(pages.get(0), 0);
            var footnoteReferencesP2 = defaultFootnoteContentExtractor.extractFootnoteContents(pages.get(1), 1);
            var footnoteReferencesP3 = defaultFootnoteContentExtractor.extractFootnoteContents(pages.get(2), 2);

            // only the first page has footnotes
            assertEquals(Map.of(), footnoteReferencesP2);
            assertEquals(Map.of(), footnoteReferencesP3);

            // Note that the line numbers only take the footnotes section into account
            var footnote1 = "1 Footnote 1";
            var footnote2 = "2 Second footnote";
            var footnote3 = "3 This is the last one";
            var expected = Map.of(
                    1, new FootnoteContent(List.of(footnote1), new TextCoordinate(0, 0, 0).extendToSelection(footnote1.length())),
                    2, new FootnoteContent(List.of(footnote2), new TextCoordinate(0, 1, 0).extendToSelection(footnote2.length())),
                    3, new FootnoteContent(List.of(footnote3), new TextCoordinate(0, 2, 0).extendToSelection(footnote3.length()))
            );
            assertEquals(expected, footnoteReferencesP1);
        }
    }
}
