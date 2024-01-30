package nitpeek.io.pdf.convenience;

import com.google.common.jimfs.Jimfs;
import nitpeek.core.api.analyze.Rule;
import nitpeek.core.api.process.RuleSetProvider;
import nitpeek.core.api.report.ReportingException;
import nitpeek.core.api.translate.Translation;
import nitpeek.core.impl.analyze.analyzer.LiteralReplacer;
import nitpeek.core.impl.process.ListPageConsumer;
import nitpeek.core.impl.translate.DefaultFallbackEnglishTranslation;
import nitpeek.io.pdf.PdfPageSource;
import nitpeek.io.pdf.testutil.FileUtil;
import nitpeek.io.pdf.testutil.PdfCreator;
import nitpeek.io.pdf.testutil.TestFile;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
final class PdfAnnotatorShould {

    private static final String INPUT_TEST_FOLDER = "In_PdfAnnotator_TestDir";
    private static final String OUTPUT_TEST_FOLDER = "Out_PdfAnnotator_TestDir";
    private final FileSystem fileSystem = Jimfs.newFileSystem();
    private final Path outputDir = FileUtil.testDir(fileSystem, OUTPUT_TEST_FOLDER);
    private final Translation i18n = new DefaultFallbackEnglishTranslation();

    @Mock private Rule rule;

    @Mock private RuleSetProvider ruleSetProvider;


    @BeforeEach
    void setup() throws IOException {
        Files.createDirectory(outputDir);
        when(ruleSetProvider.getRules()).thenReturn(Set.of(rule));
        when(rule.createAnalyzer()).thenReturn(new LiteralReplacer("line", "replace", true));
    }

    // We generate the input file in memory because it is unwieldy to read it from disk using the API of PdfAnnotator
    // (which is, granted, not a particularly clean/reusable API. It is convenient, though).
    // We write to a memory-bound filesystem because it is both faster and simpler than generating new files on
    // disk and avoids the headaches associated with that.
    @Test
    void createPdfWithSameContentsInTargetFolder() throws IOException, ReportingException {
        var pdfCreator = new PdfCreator(TestFile.getContent());

        var pdfIn = createPdf("testSameContents.pdf");
        var pdfOut = PdfAnnotator.outputPath(pdfIn, outputDir);
        pdfCreator.createPdf(pdfIn);

        var pdfSource = PdfPageSource.createFrom(Files.newInputStream(pdfIn));
        var pagesOriginal = new ListPageConsumer(pdfSource);

        var pdfAnnotator = new PdfAnnotator(ruleSetProvider, i18n);
        pdfAnnotator.annotateFeatures(pdfIn, outputDir);


        var pagesAnnotated = new ListPageConsumer(PdfPageSource.createFrom(Files.newInputStream(pdfOut)));
        // PDF text contents as seen through a PageSource serve as proxy for the actual contents of the files.
        // The compared files are not expected to satisfy binary equality due to the presence of annotations in the
        // generated file. Comparing anything more than text is too complex to be worth the effort.
        var expected = pagesOriginal.getPages();
        var actual = pagesAnnotated.getPages();

        assertEquals(expected, actual);
    }

    @Test
    void annotateGeneratedPdf() throws IOException, ReportingException {
        var pdfIn = createPdf("testAnnotations");

        var pdfAnnotator = new PdfAnnotator(ruleSetProvider, i18n);
        pdfAnnotator.annotateFeatures(pdfIn, outputDir);
        var pdfOut = PdfAnnotator.outputPath(pdfIn, outputDir);

        var randomAccess = new RandomAccessReadBuffer(Files.newInputStream(pdfOut));
        var inputPdf = Loader.loadPDF(randomAccess);

        // To simplify the check, an analyzer was picked that would produce features for each page of the test input
        // (searching for the literal value 'line')
        assertAllPagesHaveAnnotations(inputPdf);
    }

    private void assertAllPagesHaveAnnotations(PDDocument pdf) throws IOException {
        for (var page : pdf.getPages()) {
            assertFalse(page.getAnnotations().isEmpty());
        }
    }

    private Path createPdf(String filename) throws IOException {
        var pdfCreator = new PdfCreator(TestFile.getContent());

        var pdfIn = FileUtil.testFile(fileSystem, INPUT_TEST_FOLDER, filename);
        pdfCreator.createPdf(pdfIn);
        return pdfIn;
    }
}