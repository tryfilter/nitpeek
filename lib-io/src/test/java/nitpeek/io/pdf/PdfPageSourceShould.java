package nitpeek.io.pdf;

import com.google.common.jimfs.Jimfs;
import nitpeek.core.api.common.TextPage;
import nitpeek.core.impl.process.ListPageConsumer;
import nitpeek.core.impl.process.StringPageSource;
import nitpeek.io.pdf.testutil.PdfCreator;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class PdfPageSourceShould {


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

        var fileSystem = Jimfs.newFileSystem();
        var pdf = testFile(fileSystem, "testFile.pdf");
        createPdf(pdf);

        var pdfSource = PdfPageSource.createFrom(Files.newInputStream(pdf));
        var consumer = new ListPageConsumer(pdfSource);

        var expected = expectedPages();
        var actual = consumer.getPages();

        assertEquals(expected, actual);
    }


    private void createPdf(Path pdfLocation) throws IOException {
        var pages = createPages();
        new PdfCreator(new StringPageSource(pages)).createPdf(pdfLocation);
    }

    private static List<String> createPages() {
        return expectedPages().stream()
                .map(TextPage::getLines)
                .map(lines -> String.join("\n", lines))
                .toList();
    }

    private static Path testFile(FileSystem fileSystem, String filename) throws IOException {
        var testDir = testDir(fileSystem);
        Files.createDirectory(testDir);
        return testDir.resolve(filename);
    }

    private static Path testDir(FileSystem fileSystem) {
        return fileSystem.getPath("test");
    }

    private static List<TextPage> expectedPages() {
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