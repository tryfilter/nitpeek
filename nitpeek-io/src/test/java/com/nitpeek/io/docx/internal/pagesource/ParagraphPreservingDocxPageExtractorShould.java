package com.nitpeek.io.docx.internal.pagesource;

import com.nitpeek.io.testutil.TestFileParagraphs;
import com.nitpeek.core.api.report.ReportingException;
import com.nitpeek.core.impl.process.ListPageConsumer;
import com.nitpeek.io.docx.internal.pagesource.render.DefaultDocxPageRenderer;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.function.UnaryOperator;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class ParagraphPreservingDocxPageExtractorShould {

    @Test
        // more specifically, paragraphs that cross page boundaries should NOT be split, preserving their integrity,
        // enabling the transformation of each paragraph in the DOCX page into a line on a TextPage
    void extractPagesWithUnsplitParagraphs() throws IOException, Docx4JException, ReportingException {

        try (var input = DefaultDocxPageExtractorShould.class.getResourceAsStream("../../TestFile_Paragraphs.docx")) {
            var docx = WordprocessingMLPackage.load(input);

            var paragraphPreservingExtractor = new ParagraphPreservingDocxPageExtractor(docx, UnaryOperator.identity());
            var extractedPages = paragraphPreservingExtractor.extractPages();
            var extractedPagesRendered = new DefaultDocxPageRenderer().renderPages(extractedPages);
            var expectedPages = new ListPageConsumer(TestFileParagraphs.getFullContentPagesNoSplitParagraphs()).getPages();
            assertEquals(expectedPages, extractedPagesRendered);
        }
    }
}