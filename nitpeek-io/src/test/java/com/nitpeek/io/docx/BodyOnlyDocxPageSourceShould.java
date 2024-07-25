package com.nitpeek.io.docx;

import com.nitpeek.core.api.report.ReportingException;
import com.nitpeek.core.impl.process.ListPageConsumer;
import com.nitpeek.io.docx.internal.reporter.DocxPageSource;
import com.nitpeek.io.docx.internal.reporter.PageTransformers;
import com.nitpeek.io.testutil.TestFile;
import com.nitpeek.io.testutil.TestFileParagraphs;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class BodyOnlyDocxPageSourceShould {

    @Test
    void throwForInvalidFile() {
        assertThrows(ReportingException.class, () -> {
            try (var input = BodyOnlyDocxPageSourceShould.class.getResourceAsStream("../TestFile.txt")) {
                DocxPageSource.createFrom(input);
            }
        });
    }

    @Test
    void extractTextFromPages() throws IOException, ReportingException {

        try (var input = BodyOnlyDocxPageSourceShould.class.getResourceAsStream("TestFile.docx")) {

            var docxSource = DocxPageSource.createFrom(input, PageTransformers::keepOnlyBody);
            var consumer = new ListPageConsumer(docxSource);

            var expected = new ListPageConsumer(TestFile.getBodyOnlyContent()).getPages();
            var actual = consumer.getPages();

            assertEquals(expected, actual);
        }
    }

    /**
     * A more realistic test including multiline paragraphs. Such paragraphs are only "multiline" in their rendered
     * form; the DOCX page source sees each paragraph as a single line of text.
     */
    @Test
    void extractTextFromPagesWithLongerParagraphs() throws IOException, ReportingException {

        try (var input = BodyOnlyDocxPageSourceShould.class.getResourceAsStream("TestFile_Paragraphs.docx")) {

            var docxSource = DocxPageSource.createFrom(input, PageTransformers::keepOnlyBody);
            var consumer = new ListPageConsumer(docxSource);

            var expected = new ListPageConsumer(TestFileParagraphs.getBodyOnlyContent()).getPages();
            var actual = consumer.getPages();

            assertEquals(expected, actual);
        }
    }
}