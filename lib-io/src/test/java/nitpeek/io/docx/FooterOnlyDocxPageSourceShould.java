package nitpeek.io.docx;

import jakarta.xml.bind.JAXBException;
import nitpeek.core.impl.process.ListPageConsumer;
import nitpeek.io.testutil.TestFile;
import nitpeek.io.testutil.TestFileParagraphs;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class FooterOnlyDocxPageSourceShould {

    @Test
    void throwForInvalidFile() {
        assertThrows(Docx4JException.class, () -> {
            try (var input = FooterOnlyDocxPageSourceShould.class.getResourceAsStream("../TestFile.txt")) {
                DocxPageSource.createFrom(input);
            }
        });
    }

    @Test
    void extractTextFromPages() throws IOException, Docx4JException, JAXBException {

        try (var input = FooterOnlyDocxPageSourceShould.class.getResourceAsStream("TestFile.docx")) {

            var docxSource = DocxPageSource.createFrom(input, PageTransformers::keepOnlyFootnotes);
            var consumer = new ListPageConsumer(docxSource);

            var expected = new ListPageConsumer(TestFile.getFootnotesOnlyContent()).getPages();
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

        try (var input = FooterOnlyDocxPageSourceShould.class.getResourceAsStream("TestFile_Paragraphs.docx")) {

            var docxSource = DocxPageSource.createFrom(input, PageTransformers::keepOnlyFootnotes);
            var consumer = new ListPageConsumer(docxSource);

            var expected = new ListPageConsumer(TestFileParagraphs.getFootnotesOnlyContent()).getPages();
            var actual = consumer.getPages();

            assertEquals(expected, actual);
        }
    }
}