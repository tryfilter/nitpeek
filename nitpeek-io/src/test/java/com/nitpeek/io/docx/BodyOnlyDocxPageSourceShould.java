package com.nitpeek.io.docx;

import com.nitpeek.io.testutil.TestFile;
import com.nitpeek.io.testutil.TestFileParagraphs;
import jakarta.xml.bind.JAXBException;
import com.nitpeek.core.impl.process.ListPageConsumer;
import com.nitpeek.io.docx.internal.reporter.PageTransformers;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class BodyOnlyDocxPageSourceShould {

    @Test
    void throwForInvalidFile() {
        assertThrows(Docx4JException.class, () -> {
            try (var input = BodyOnlyDocxPageSourceShould.class.getResourceAsStream("../TestFile.txt")) {
                DocxPageSource.createFrom(input);
            }
        });
    }

    @Test
    void extractTextFromPages() throws IOException, Docx4JException, JAXBException {

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
    void extractTextFromPagesWithLongerParagraphs() throws IOException, Docx4JException, JAXBException {

        try (var input = BodyOnlyDocxPageSourceShould.class.getResourceAsStream("TestFile_Paragraphs.docx")) {

            var docxSource = DocxPageSource.createFrom(input, PageTransformers::keepOnlyBody);
            var consumer = new ListPageConsumer(docxSource);

            var expected = new ListPageConsumer(TestFileParagraphs.getBodyOnlyContent()).getPages();
            var actual = consumer.getPages();

            assertEquals(expected, actual);
        }
    }
}