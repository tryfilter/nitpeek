package nitpeek.io.docx.internal.pagesource;

import jakarta.xml.bind.JAXBException;
import nitpeek.core.impl.process.ListPageConsumer;
import nitpeek.io.docx.internal.pagesource.render.DefaultDocxPageRenderer;
import nitpeek.io.testutil.TestFileParagraphs;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.function.UnaryOperator;

import static org.junit.jupiter.api.Assertions.*;

final class DefaultDocxPageExtractorShould {

    @Test
        // more specifically, paragraphs that cross page boundaries should be split so that any content that "appears" to
        // be part of a page in Word also is part of the extracted page
    void extractPagesAsRenderedByWord() throws IOException, Docx4JException, JAXBException {

        try (var input = DefaultDocxPageExtractorShould.class.getResourceAsStream("../../TestFile_Paragraphs.docx")) {
            var docx = WordprocessingMLPackage.load(input);

            var defaultExtractor = new DefaultDocxPageExtractor(docx, UnaryOperator.identity());
            var extractedPages = defaultExtractor.extractPages();
            var extractedPagesRendered = new DefaultDocxPageRenderer().renderPages(extractedPages);
            var expectedPages = new ListPageConsumer(TestFileParagraphs.getFullContentPagesAsSeen()).getPages();
            assertEquals(expectedPages, extractedPagesRendered);
        }
    }
}