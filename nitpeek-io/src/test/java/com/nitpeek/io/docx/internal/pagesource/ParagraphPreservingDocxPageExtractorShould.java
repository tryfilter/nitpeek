package com.nitpeek.io.docx.internal.pagesource;

import com.nitpeek.core.api.report.ReportingException;
import com.nitpeek.core.impl.process.ListPageConsumer;
import com.nitpeek.io.docx.internal.pagesource.render.DefaultDocxPageRenderer;
import com.nitpeek.io.docx.internal.reporter.PageTransformers;
import com.nitpeek.io.testutil.TestFileParagraphs;
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

    @Test
    void extractPagesWithLongParagraphFull() throws IOException, Docx4JException, ReportingException {

        try (var input = DefaultDocxPageExtractorShould.class.getResourceAsStream("../../TestFile_Long_Paragraph.docx")) {

            var docx = WordprocessingMLPackage.load(input);

            var paragraphPreservingExtractor = new ParagraphPreservingDocxPageExtractor(docx, UnaryOperator.identity());
            var extractedPages = paragraphPreservingExtractor.extractPages();
            var extractedPagesRendered = new DefaultDocxPageRenderer().renderPages(extractedPages);
            var expectedPages = new ListPageConsumer(TestFileParagraphs.getFullPagesLongParagraph()).getPages();
            assertEquals(expectedPages, extractedPagesRendered);
        }
    }

    @Test
    void extractPagesWithLongParagraphBodyOnly() throws IOException, Docx4JException, ReportingException {

        try (var input = DefaultDocxPageExtractorShould.class.getResourceAsStream("../../TestFile_Long_Paragraph.docx")) {

            var docx = WordprocessingMLPackage.load(input);

            var paragraphPreservingExtractorBodyOnly = new ParagraphPreservingDocxPageExtractor(docx, PageTransformers::keepOnlyBody);
            var extractedPageBodies = paragraphPreservingExtractorBodyOnly.extractPages();
            var extractedPageBodiesRendered = new DefaultDocxPageRenderer().renderPages(extractedPageBodies);
            var expectedPages = new ListPageConsumer(TestFileParagraphs.getBodyOnlyPagesLongParagraph()).getPages();
            assertEquals(expectedPages, extractedPageBodiesRendered);
        }
    }
}