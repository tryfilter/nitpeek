package nitpeek.io.pdf.internal;

import nitpeek.core.api.common.Feature;
import nitpeek.core.api.common.TextCoordinate;
import nitpeek.core.api.common.TextPage;
import nitpeek.core.api.common.TextSelection;
import nitpeek.core.impl.common.AnonymousSingleComponentFeature;
import nitpeek.core.impl.process.ListPageConsumer;
import nitpeek.io.pdf.PdfPageSource;
import nitpeek.io.pdf.internal.SectionExtractor.ComponentWithSections;
import nitpeek.io.pdf.internal.SectionExtractor.Section;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class SectionExtractorShould {

    private static final String TEST_PDF = "../TestFile.pdf";
    private final PDDocument testPdf = getTestPdf();

    private final List<TextPage> pages = new ListPageConsumer(PdfPageSource.createFrom(getPdfStream())).getPages();

    SectionExtractorShould() throws IOException {
    }

    @AfterEach
    void tearDown() throws IOException {
        testPdf.close();
    }

    @Test
    void extractNoSectionsForEmptyFeatureList() throws IOException {

        List<Feature> emptyFeatures = List.of();
        var extractor = new SectionExtractor(emptyFeatures, pages);

        List<ComponentWithSections> expected = List.of();
        var actual = extractor.getSections(testPdf);

        assertEquals(expected, actual);
    }

    @Test
    void extractSectionForSingleLetterFeature() throws IOException {

        // "L" in "Single-Line Page"
        assertEndpointsStartingAt(new TextCoordinate(1, 1, 7), "L");
    }

    @Test
    void extractSectionForWordFeature() throws IOException {

        // "special" in "Some special characters: üÄß^°ä#`'”"
        assertEndpointsStartingAt(new TextCoordinate(0, 6, 5), "special");
    }

    @Test
    void extractSectionForPartialWordFeature() throws IOException {

        // "spec" in "Some special characters: üÄß^°ä#`'”"
        assertEndpointsStartingAt(new TextCoordinate(0, 6, 5), "spec");
    }

    @Test
    void extractSectionForMultipleWordsFeature() throws IOException {

        // "Footnote:1 another footnote:" in "Footnote:1 another footnote: 2"
        assertEndpointsStartingAt(new TextCoordinate(0, 10, 0), "Footnote:1 another footnote:");
    }

    @Test
    void extractSectionForCrossWordFeature() throws IOException {

        // "wing this line there will be three mo" in "Following this line there will be three more lines."
        assertEndpointsStartingAt(new TextCoordinate(2, 1, 5), "wing this line there will be three mo");
    }

    @Test
    void extractSectionsForCrossFullLinesFeature() throws IOException {

        var matchRegion = new TextSelection(new TextCoordinate(2, 1, 0), new TextCoordinate(2, 4, 23));
        List<Feature> feature = List.of(new AnonymousSingleComponentFeature(matchRegion));
        var extractor = new SectionExtractor(feature, pages);

        var componentSections = extractor.getSections(testPdf);
        var expected = new SectionEndpoints[]{
                new SectionEndpoints("Following this line there will be three more lines."),
                new SectionEndpoints("Second line Different Font ending in a hyphen-"),
                new SectionEndpoints("ated word. Technically the third line contains part of it as well"),
                new SectionEndpoints("Special string: (marker)")
        };

        assertEndpoints(componentSections, expected);
    }

    @Test
    void extractSectionsCrossPartialLinesFeature() throws IOException {

        var matchRegion = new TextSelection(new TextCoordinate(2, 1, 12), new TextCoordinate(2, 4, 3));
        List<Feature> feature = List.of(new AnonymousSingleComponentFeature(matchRegion));
        var extractor = new SectionExtractor(feature, pages);

        var componentSections = extractor.getSections(testPdf);
        var expected = new SectionEndpoints[]{
                new SectionEndpoints("ine there will be three more lines."),
                new SectionEndpoints("Second line Different Font ending in a hyphen-"),
                new SectionEndpoints("ated word. Technically the third line contains part of it as well"),
                new SectionEndpoints("Spec")
        };

        assertEndpoints(componentSections, expected);
    }

    private void assertEndpointsStartingAt(TextCoordinate componentStart, String expectedMatch) throws IOException {

        var textRegion = componentStart.extendToSelection(expectedMatch.length());
        List<Feature> feature = List.of(new AnonymousSingleComponentFeature(textRegion));
        var extractor = new SectionExtractor(feature, pages);

        var componentSections = extractor.getSections(testPdf);

        assertEndpoints(componentSections, new SectionEndpoints(expectedMatch));
    }

    private record SectionEndpoints(String start, String end) {
        public SectionEndpoints(String section) {
            this(section.substring(0, 1), section.substring(section.length() - 1));
        }
    }

    private void assertEndpoints(List<ComponentWithSections> sections, SectionEndpoints... endpoints) {
        assertEquals(1, sections.size());
        var subsections = sections.getFirst().sections();
        assertEquals(endpoints.length, subsections.size());


        for (int i = 0; i < endpoints.length; i++) {
            assertSubsection(subsections.get(i), endpoints[i]);
        }
    }

    private void assertSubsection(Section section, SectionEndpoints endpoints) {
        String actualStartChar = section.start().position().getUnicode();
        String actualEndChar = section.end().position().getUnicode();

        assertEquals(endpoints.start(), actualStartChar);
        assertEquals(endpoints.end(), actualEndChar);
    }

    private PDDocument getTestPdf() throws IOException {
        try (var input = getPdfStream()) {
            return Loader.loadPDF(new RandomAccessReadBuffer(input));
        }
    }

    private InputStream getPdfStream() {
        return SectionExtractorShould.class.getResourceAsStream(TEST_PDF);
    }
}