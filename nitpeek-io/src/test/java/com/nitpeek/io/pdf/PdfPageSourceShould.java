package com.nitpeek.io.pdf;

import com.nitpeek.core.api.common.TextPage;
import com.nitpeek.core.impl.process.ListPageConsumer;
import com.nitpeek.io.testutil.TestFile;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class PdfPageSourceShould {


    @Test
    void throwForInvalidFile() {
        assertThrows(IOException.class, () -> {
            try (var input = PdfPageSourceShould.class.getResourceAsStream("../TestFile.txt")) {
                PdfPageSource.createFrom(input);
            }
        });
    }

    @Test
    void extractTextFromPages() throws IOException {

        try (var input = PdfPageSourceShould.class.getResourceAsStream("TestFile.pdf")) {

            var pdfSource = PdfPageSource.createFrom(input);
            var consumer = new ListPageConsumer(pdfSource);

            var expected = expectedPages();
            var actual = consumer.getPages();

            assertEquals(expected, actual);
        }
    }
    
    private static List<TextPage> expectedPages() {

        ListPageConsumer result = new ListPageConsumer(TestFile.getContent());

        return result.getPages();
    }
}