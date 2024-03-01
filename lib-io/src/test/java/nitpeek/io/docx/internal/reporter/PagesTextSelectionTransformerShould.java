package nitpeek.io.docx.internal.reporter;

import jakarta.xml.bind.JAXBException;
import nitpeek.core.api.common.TextCoordinate;
import nitpeek.core.api.common.TextSelection;
import nitpeek.io.docx.internal.pagesource.*;
import nitpeek.io.docx.render.DocxTextSelection;
import nitpeek.util.collection.ListEnds;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Note that for DOCX documents, cross-page selections are subtle: they only contain the contents of the body of each
 * page, skipping any footnotes and footer/header text in between.<br>
 * While the selection transformer under test abstracts this fact away, rendering the resulting {@code DocxTextSelection}
 * in an actual DOCX document may not make sense. <br>
 * <br>
 * Note that each paragraph corresponds to a line in terms of {@link nitpeek.core.api.common.TextPage} coordinates.
 */
final class PagesTextSelectionTransformerShould {

    private final List<? extends SegmentedDocxPage> pages;

    PagesTextSelectionTransformerShould() throws Docx4JException, JAXBException {
        WordprocessingMLPackage docx = WordprocessingMLPackage.load(PagesTextSelectionTransformerShould.class.getResourceAsStream("../../TestFile_Paragraphs.docx"));
        pages = new DefaultDocxPageExtractor(docx).extractPages();
    }


    @Test
    void returnEmptySelectionWhenPagesEmpty() {
        var arbitrarySelection = new TextCoordinate(0, 4, 12).extendToSelection(6);
        var transformer = new PagesTextSelectionTransformer(List.of(), this::getParagraphRenderer);

        DocxTextSelection result = transformer.transform(arbitrarySelection);
        assertEquals(List.of(), result.segments());
    }

    @Test
    void returnSingleWordSelection() {
        int page = 0;
        var selection = new TextCoordinate(page, 2, 5).extendToSelection(6); // simple
        var transformer = new PagesTextSelectionTransformer(pages, this::getParagraphRenderer);

        DocxTextSelection result = transformer.transform(selection);
        assertEquals(List.of("simple"), renderDocxSelection(result, getParagraphRenderer(page)));
    }

    @Test
    void returnFullRunSelection() {
        int page = 0;
        String fullRun = "Some simple test in a paragraph.";
        var selection = new TextCoordinate(page, 2, 0).extendToSelection(fullRun.length());
        var transformer = new PagesTextSelectionTransformer(pages, this::getParagraphRenderer);

        DocxTextSelection result = transformer.transform(selection);
        assertEquals(List.of(fullRun), renderDocxSelection(result, getParagraphRenderer(page)));
    }

    @Test
    void returnCrossRunSelection() {
        int page = 2;
        String crossRunString = "ne Diff";
        var selection = new TextCoordinate(page, 2, 9).extendToSelection(crossRunString.length());
        var transformer = new PagesTextSelectionTransformer(pages, this::getParagraphRenderer);

        DocxTextSelection result = transformer.transform(selection);
        assertEquals(List.of(crossRunString), renderDocxSelection(result, getParagraphRenderer(page)));
    }

    @Test
    void returnAcrossMultipleRunsSelection() {
        int page = 2;
        String crossRunString = "cond line Different Font ending i";
        var selection = new TextCoordinate(page, 2, 2).extendToSelection(crossRunString.length());
        var transformer = new PagesTextSelectionTransformer(pages, this::getParagraphRenderer);

        DocxTextSelection result = transformer.transform(selection);
        assertEquals(List.of(crossRunString), renderDocxSelection(result, getParagraphRenderer(page)));
    }

    @Test
    void returnCrossParagraphSelection() {
        int page = 0;
        var paragraphs = List.of(
                "Skipped 2 lines.",
                "Italicized text."
        );
        var selection = new TextSelection(new TextCoordinate(page, 4, 0), new TextCoordinate(page, 5, 15));
        var transformer = new PagesTextSelectionTransformer(pages, this::getParagraphRenderer);

        DocxTextSelection result = transformer.transform(selection);
        assertEquals(paragraphs, renderDocxSelection(result, getParagraphRenderer(page)));
    }

    @Test
    void returnFullParagraphSelection() {
        int page = 0;
        String paragraph = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. In nec dolor finibus, porta arcu " +
                "sagittis, porttitor nulla. Donec mattis, tellus in ultrices pulvinar, dui turpis hendrerit quam, sit " +
                "amet sollicitudin est tortor non lectus. Nulla semper nisl libero, a ornare velit cursus ac. Mauris ut " +
                "suscipit ante. Phasellus vestibulum lorem tellus. Morbi mollis tellus et libero finibus efficitur. " +
                "Vestibulum non lacinia mauris, ut fringilla velit. Sed laoreet nisi non maximus molestie. Aliquam " +
                "convallis, purus at molestie congue, ipsum lorem dapibus mi, vel malesuada neque ex sit amet erat. " +
                "Suspendisse sit amet purus sit amet lacus sodales finibus. Orci varius natoque penatibus et magnis dis " +
                "parturient montes, nascetur ridiculus mus. Etiam eu odio eu lacus venenatis pretium. Nulla tincidunt " +
                "lectus risus, vel dapibus sem elementum eu. Donec vel bibendum justo. Donec tristique ut ligula vel " +
                "euismod. Aliquam ligula velit, consequat vel placerat id, blandit sit amet nibh.";
        var selection = new TextCoordinate(page, 12, 0).extendToSelection(paragraph.length());
        var transformer = new PagesTextSelectionTransformer(pages, this::getParagraphRenderer);

        DocxTextSelection result = transformer.transform(selection);
        assertEquals(List.of(paragraph), renderDocxSelection(result, getParagraphRenderer(page)));
    }

    @Test
    void returnFullFootnoteSelection() {
        int page = 0;
        var footnoteLines = List.of(
                "2Second footnote, which contains multiple lines",
                "Second line of Footnote #2 here."
        );
        var selection = new TextSelection(new TextCoordinate(page, 15, 0), new TextCoordinate(page, 16, 31));
        var transformer = new PagesTextSelectionTransformer(pages, this::getParagraphRenderer);

        DocxTextSelection result = transformer.transform(selection);
        assertEquals(footnoteLines, renderDocxSelection(result, getParagraphRenderer(page)));
    }

    @Test
    void returnPartialFootnoteSelection() {
        int page = 0;
        String footnotePart = "ond footnote, whi";
        var selection = new TextCoordinate(page, 15, 4).extendToSelection(footnotePart.length());
        var transformer = new PagesTextSelectionTransformer(pages, this::getParagraphRenderer);

        DocxTextSelection result = transformer.transform(selection);
        assertEquals(List.of(footnotePart), renderDocxSelection(result, getParagraphRenderer(page)));
    }

    /**
     * Only crosses a single page boundary and (crucially) only contains one footer (otherwise the page meta-fields
     * would be rendered incorrectly).<br>
     * <br>
     * Note that the selection contains both body text, footnotes, footer, and header text. While the
     * {@code SelectionTransformer} is able to produce such selections, it may not make sense to highlight them as one
     * contiguous section of text in a DOCX document.
     */
    @Test
    void returnCrossPageSelection() {
        int page = 0;
        var footnoteLines = List.of(
                "mentum semper quis eros. Aenean metus nunc, lobortis quis quam ac, lacinia lobortis nisi. Nullam porta ipsum ipsum, a mattis ",
                "1Footnote 1",
                "2Second footnote, which contains multiple lines",
                "Second line of Footnote #2 here.",
                "3This is the last one",
                "Footer",
                "Lines",
                "3 total",
                "Page 1/3",
                "HEADER Text",
                "lorem dictum ac. Donec vel leo convallis, tincidunt dui sit amet, lobortis justo. Sed rhoncus vel odio ut vestib"
        );
        var selection = new TextSelection(new TextCoordinate(page, 13, 484), new TextCoordinate(page + 1, 1, 111));
        var transformer = new PagesTextSelectionTransformer(pages, this::getParagraphRenderer);

        DocxTextSelection result = transformer.transform(selection);
        assertEquals(footnoteLines, renderDocxSelection(result, getParagraphRenderer(page)));
    }

    private ParagraphRenderer getParagraphRenderer(int currentPage) {
        return getParagraphRenderer(currentPage, pages.size());
    }

    // Convenience to enable method-reference use
    private ParagraphRenderer getParagraphRenderer(int currentPage, int pageCount) {
        return new SimpleParagraphRenderer(currentPage, pageCount, new SimpleArabicNumberRenderer());

    }

    // Note that all segments of the selection should belong to the same page, otherwise the paragraphRenderer can
    // evaluate special placeholders (e.g. current page) incorrectly.
    // This means that selections that cross page boundaries are not fully supported in the test.
    private List<String> renderDocxSelection(DocxTextSelection docxTextSelection, ParagraphRenderer paragraphRenderer) {
        var segmentRenderer = new DefaultSegmentRenderer(paragraphRenderer);

        var segmentsSplit = new ListEnds<>(docxTextSelection.segments());
        var firstSegment = segmentRenderer.render(segmentsSplit.first());

        List<String> result = new ArrayList<>(firstSegment);
        truncateFirstFrom(docxTextSelection.indexOfFirstCharacter(), result);

        for (var fullSegment : segmentsSplit.middle()) {
            result.addAll(segmentRenderer.render(fullSegment));
        }

        var lastSegment = segmentRenderer.render(segmentsSplit.last());
        if (segmentsSplit.first() != segmentsSplit.last())
            result.addAll(lastSegment); // only add last segment if it's distinct from the first


        truncateLastTo(docxTextSelection.indexOfLastCharacter(), result, segmentsSplit.last(), paragraphRenderer);

        return result;
    }

    private void truncateFirstFrom(int firstCharacter, List<String> lines) {
        lines.set(0, lines.get(0).substring(firstCharacter));
    }

    private void truncateLastTo(int lastCharacter, List<String> lines, DocxSegment lastSegment, ParagraphRenderer paragraphRenderer) {

        var lastParagraph = lastSegment.paragraphs().getLast();
        String lastRunRendered = paragraphRenderer.renderBetween(lastSegment.indexOfLastRun(), lastSegment.indexOfLastRun(), lastParagraph);
        int excessCharacters = lastRunRendered.length() - 1 - lastCharacter;
        int lastIndex = lines.size() - 1;
        int afterFinalCharacter = lines.get(lastIndex).length() - excessCharacters;
        lines.set(lastIndex, lines.get(lastIndex).substring(0, afterFinalCharacter));
    }

}