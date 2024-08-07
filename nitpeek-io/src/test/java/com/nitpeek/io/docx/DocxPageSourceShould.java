package com.nitpeek.io.docx;

import com.nitpeek.core.api.report.ReportingException;
import com.nitpeek.core.impl.process.ListPageConsumer;
import com.nitpeek.io.docx.internal.reporter.DocxPageSource;
import com.nitpeek.io.testutil.TestFile;
import com.nitpeek.io.testutil.TestFileParagraphs;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class DocxPageSourceShould {

    @Test
    void throwForInvalidFile() {
        assertThrows(ReportingException.class, () -> {
            try (var input = DocxPageSourceShould.class.getResourceAsStream("../TestFile.txt")) {
                DocxPageSource.createFrom(input);
            }
        });
    }

    /**
     * This test is somewhat contrived because none of the lines in the test document are longer than the page width.
     * Because of this, Word has generated a separate paragraph for each line, which gives the expected line separation.
     * This is convenient for the test, since we can reuse the same test data as we have for the PDF page source test.
     * For the general case, however, a paragraph may span multiple lines, making it impossible to detect line breaks.
     * Consequently, there is no robust relationship between the lines in the TextPages produced by the DOCX page source
     * and the actual rows rendered on-screen for the document.
     */
    @Test
    void extractTextFromPages() throws IOException, ReportingException {

        try (var input = DocxPageSourceShould.class.getResourceAsStream("TestFile.docx")) {

            var docxSource = DocxPageSource.createFrom(input);
            var consumer = new ListPageConsumer(docxSource);

            var expected = new ListPageConsumer(TestFile.getContent()).getPages();
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

        try (var input = DocxPageSourceShould.class.getResourceAsStream("TestFile_Paragraphs.docx")) {

            var docxSource = DocxPageSource.createFrom(input);
            var consumer = new ListPageConsumer(docxSource);

            var expected = new ListPageConsumer(TestFileParagraphs.getFullContentPagesAsSeen()).getPages();
            var actual = consumer.getPages();

            assertEquals(expected, actual);
        }
    }
}