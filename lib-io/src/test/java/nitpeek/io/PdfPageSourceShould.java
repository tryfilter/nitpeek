package nitpeek.io;

import nitpeek.core.api.common.TextPage;
import nitpeek.core.impl.process.ListPageConsumer;
import nitpeek.core.impl.process.StringPageSource;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PdfPageSourceShould {

    @Test
    void throwForInvalidFile() {
        assertThrows(IOException.class, () -> {
            try (var input = PdfPageSourceShould.class.getResourceAsStream("TestFile.txt")) {
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

    private List<TextPage> expectedPages() {
        var pages = List.of(
                """
                        HEADER Text
                        Main Heading
                        Some simple test in a paragraph.
                        Next line.
                        Skipped 2 lines.
                        Italicized text.
                        Some special characters: üÄß^°ä#`'”
                        “Quote”
                        “”
                        «Other quote»
                        Footnote:1 another footnote: 2
                        And a third footnote3
                        1 Footnote 1
                        2 Second footnote
                        3 This is the last one
                        Footer
                        Lines
                        3 total
                        Page 1/3""",
                """
                        HEADER Text
                        Single-Line Page
                        Footer
                        Lines
                        3 total
                        Page 2/3""",
                """
                        HEADER Text
                        Following this line there will be three more lines.
                        Second line Different Font ending in a hyphen-
                        ated word. Technically the third line contains part of it as well
                        Special string: (marker)
                        Footer
                        Lines
                        3 total
                        Page 3/3"""
        );

        ListPageConsumer result = new ListPageConsumer(new StringPageSource(pages));

        return result.getPages();
    }
}