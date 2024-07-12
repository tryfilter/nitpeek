package com.nitpeek.io.docx.internal.reporter;

import com.nitpeek.io.docx.internal.pagesource.render.*;
import jakarta.xml.bind.JAXBException;
import com.nitpeek.core.api.common.TextCoordinate;
import com.nitpeek.core.api.common.TextSelection;
import com.nitpeek.io.docx.internal.common.DocxTextSelection;
import com.nitpeek.io.docx.internal.common.RunRenderer;
import com.nitpeek.io.docx.internal.pagesource.DefaultDocxPageExtractor;
import com.nitpeek.io.docx.types.CompositeRun;
import com.nitpeek.io.docx.types.SplittableRun;
import com.nitpeek.io.util.collection.ListEnds;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Note that for DOCX documents, cross-page selections are subtle: they only contain the contents of the body of each
 * page, skipping any footnotes and footer/header text in between.<br>
 * While the selection transformer under test abstracts this fact away, rendering the resulting {@code DocxTextSelection}
 * in an actual DOCX document may not make sense. <br>
 * <br>
 * Note that each paragraph corresponds to a line in terms of {@link com.nitpeek.core.api.common.TextPage} coordinates.<br>
 * Note also that these tests use the file resources/nitpeek/io/docx/TestFile_Paragraphs.docx as input.
 */
final class PagesTextSelectionTransformerShould {

    private final List<? extends SegmentedDocxPage<? extends CompositeRun>> pages;
    private final List<? extends SegmentedDocxPage<? extends CompositeRun>> bodyOnlyPages;
    private final List<? extends SegmentedDocxPage<? extends CompositeRun>> footnotesOnlyPages;

    PagesTextSelectionTransformerShould() throws Docx4JException, JAXBException {
        WordprocessingMLPackage docx = WordprocessingMLPackage.load(PagesTextSelectionTransformerShould.class.getResourceAsStream("../../TestFile_Paragraphs.docx"));
        this.pages = new DefaultDocxPageExtractor(docx, UnaryOperator.identity()).extractPages();
        this.bodyOnlyPages = new DefaultDocxPageExtractor(docx, PageTransformers::keepOnlyBody).extractPages();
        this.footnotesOnlyPages = new DefaultDocxPageExtractor(docx, PageTransformers::keepOnlyFootnotes).extractPages();
    }

    @Test
    void returnEmptySelectionWhenPagesEmpty() {
        var arbitrarySelection = new TextCoordinate(0, 4, 12).extendToSelection(6);
        var transformer = new PagesTextSelectionTransformer(List.of(), this::getRunRenderer);

        var docxTextSelection = transformer.transform(arbitrarySelection);
        assertEquals(List.of(), docxTextSelection.segment().paragraphs());
    }

    @Test
    void returnSingleWordSelection() {
        int page = 0;
        TextSelection selection = new TextCoordinate(page, 2, 5).extendToSelection(6); // simple
        var transformer = new PagesTextSelectionTransformer(pages, this::getRunRenderer);

        var docxTextSelection = transformer.transform(selection);
        assertEquals(List.of("simple"), renderDocxSelection(docxTextSelection, getRunRenderer(page)));
    }

    @Test
    void returnFullRunSelection() {
        int page = 0;
        String fullRun = "Some simple test in a paragraph.";
        TextSelection selection = new TextCoordinate(page, 2, 0).extendToSelection(fullRun.length());
        var transformer = new PagesTextSelectionTransformer(pages, this::getRunRenderer);

        var docxTextSelection = transformer.transform(selection);
        assertEquals(List.of(fullRun), renderDocxSelection(docxTextSelection, getRunRenderer(page)));
    }

    @Test
    void returnConsecutiveSelectionsFromSameRun() {
        int page = 0;
        int firstSelectionLength = 10;
        var transformer = new PagesTextSelectionTransformer(pages, this::getRunRenderer);
        String fullRun = "Some simple test in a paragraph.";
        TextSelection selection1 = new TextCoordinate(page, 2, 0).extendToSelection(firstSelectionLength);
        TextSelection selection2 = new TextCoordinate(page, 2, firstSelectionLength).extendToSelection(fullRun.length() - firstSelectionLength);
        var docxTextSelection1 = transformer.transform(selection1);
        var docxTextSelection2 = transformer.transform(selection2);
        assertEquals(List.of(fullRun.substring(0, firstSelectionLength)), renderDocxSelection(docxTextSelection1, getRunRenderer(page)));
        assertEquals(List.of(fullRun.substring(firstSelectionLength)), renderDocxSelection(docxTextSelection2, getRunRenderer(page)));
    }

    @Test
    void returnIntersectingSelectionsFromSameRun() {
        int page = 0;
        int selectionLength = 10;
        var transformer = new PagesTextSelectionTransformer(pages, this::getRunRenderer);
        String fullRun = "Some simple test in a paragraph.";
        TextSelection selection1 = new TextCoordinate(page, 2, 0).extendToSelection(selectionLength);
        TextSelection selection2 = new TextCoordinate(page, 2, 3).extendToSelection(selectionLength);
        var docxTextSelection1 = transformer.transform(selection1);
        var docxTextSelection2 = transformer.transform(selection2);
        assertEquals(List.of(fullRun.substring(0, selectionLength)), renderDocxSelection(docxTextSelection1, getRunRenderer(page)));
        assertEquals(List.of(fullRun.substring(3, selectionLength + 3)), renderDocxSelection(docxTextSelection2, getRunRenderer(page)));
    }

    @Test
    void reuseSameCompositeRunWhenTransformingTwoSelectionsInSameOriginalRun() {
        int page = 0;
        int selectionLength = 10;
        var transformer = new PagesTextSelectionTransformer(pages, this::getRunRenderer);
        // fullRun = "Some simple test in a paragraph.";
        TextSelection selection1 = new TextCoordinate(page, 2, 0).extendToSelection(selectionLength); // Some simpl
        TextSelection selection2 = new TextCoordinate(page, 2, 3).extendToSelection(selectionLength); // e simple t
        var docxTextSelection1 = transformer.transform(selection1);
        var docxTextSelection2 = transformer.transform(selection2);
        var selection1Runs = docxTextSelection1.segment().componentRuns();
        var selection2Runs = docxTextSelection2.segment().componentRuns();

        assertEquals(1, selection1Runs.size());
        assertEquals(1, selection2Runs.size());

        SplittableRun singleBackingRunSelection1 = selection1Runs.getFirst();
        SplittableRun singleBackingRunSelection2 = selection2Runs.getFirst();
        assertSame(singleBackingRunSelection1, singleBackingRunSelection2);
    }

    /**
     * This is an edge case that related to headers and footers.
     * Since in DOCX documents, multiple pages can share the same header/footer, it must be guaranteed that extracting
     * a run contained in a header/footer always generates the same (object-identical) wrapping {@code CompositeRun}.
     */
    @Test
    void reuseSameCompositeRunWhenTransformingTwoSelectionsInSameOriginalFooterRunFromDifferentPages() {

        var transformer = new PagesTextSelectionTransformer(pages, this::getRunRenderer);
        TextSelection selection1 = new TextCoordinate(0, 21, 0).extendToSelection(2); // [Page 1 Footer] Pa
        TextSelection selection2 = new TextCoordinate(1, 6, 2).extendToSelection(2); // [Page 2 Footer] ge
        var docxTextSelection1 = transformer.transform(selection1);
        var docxTextSelection2 = transformer.transform(selection2);
        var selection1Runs = docxTextSelection1.segment().componentRuns();
        var selection2Runs = docxTextSelection2.segment().componentRuns();

        assertEquals(1, selection1Runs.size());
        assertEquals(1, selection2Runs.size());

        SplittableRun singleBackingRunSelection1 = selection1Runs.getFirst();
        SplittableRun singleBackingRunSelection2 = selection2Runs.getFirst();
        assertSame(singleBackingRunSelection1, singleBackingRunSelection2);
    }

    @Test
    void returnCrossRunSelection() {
        int page = 2;
        String crossRunString = "ne Diff";
        TextSelection selection = new TextCoordinate(page, 2, 9).extendToSelection(crossRunString.length());
        var transformer = new PagesTextSelectionTransformer(pages, this::getRunRenderer);

        var docxTextSelection = transformer.transform(selection);
        assertEquals(List.of(crossRunString), renderDocxSelection(docxTextSelection, getRunRenderer(page)));
    }

    @Test
    void returnAcrossMultipleRunsSelection() {
        int page = 2;
        String crossRunString = "cond line Different Font ending i";
        TextSelection selection = new TextCoordinate(page, 2, 2).extendToSelection(crossRunString.length());
        var transformer = new PagesTextSelectionTransformer(pages, this::getRunRenderer);

        var docxTextSelection = transformer.transform(selection);
        assertEquals(List.of(crossRunString), renderDocxSelection(docxTextSelection, getRunRenderer(page)));
    }

    @Test
    void returnCrossParagraphSelection() {
        int page = 0;
        var paragraphs = List.of(
                "Skipped 2 lines.",
                "Italicized text."
        );
        TextSelection selection = new TextSelection(new TextCoordinate(page, 4, 0), new TextCoordinate(page, 5, 15));
        var transformer = new PagesTextSelectionTransformer(pages, this::getRunRenderer);

        var docxTextSelection = transformer.transform(selection);
        assertEquals(paragraphs, renderDocxSelection(docxTextSelection, getRunRenderer(page)));
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
        TextSelection selection = new TextCoordinate(page, 12, 0).extendToSelection(paragraph.length());
        var transformer = new PagesTextSelectionTransformer(pages, this::getRunRenderer);

        var docxTextSelection = transformer.transform(selection);
        assertEquals(List.of(paragraph), renderDocxSelection(docxTextSelection, getRunRenderer(page)));
    }

    @Test
    void returnFullFootnoteSelection() {
        int page = 0;
        var footnoteLines = List.of(
                "2Second footnote, which contains multiple lines",
                "Second line of Footnote #2 here."
        );
        var selection = new TextSelection(new TextCoordinate(page, 15, 0), new TextCoordinate(page, 16, 31));
        var transformer = new PagesTextSelectionTransformer(pages, this::getRunRenderer);

        var docxTextSelection = transformer.transform(selection);
        assertEquals(footnoteLines, renderDocxSelection(docxTextSelection, getRunRenderer(page)));
    }

    @Test
    void returnPartialFootnoteSelection() {
        int page = 0;
        String footnotePart = "ond footnote, whi";
        TextSelection selection = new TextCoordinate(page, 15, 4).extendToSelection(footnotePart.length());
        var transformer = new PagesTextSelectionTransformer(pages, this::getRunRenderer);

        var docxTextSelection = transformer.transform(selection);
        assertEquals(List.of(footnotePart), renderDocxSelection(docxTextSelection, getRunRenderer(page)));
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
        var transformer = new PagesTextSelectionTransformer(pages, this::getRunRenderer);

        var docxTextSelection = transformer.transform(selection);
        assertEquals(footnoteLines, renderDocxSelection(docxTextSelection, getRunRenderer(page)));
    }

    @Test
    void returnSelectionAdjustedForBodyOnlyPages() {
        int page = 1;
        int line = 0; // Note that if the page only contains content from the page body, the selectedText below occurs at the first line (the header line is not counted)
        String selectedText = "tum ac. Do";
        TextSelection selection = new TextCoordinate(page, line, 9).extendToSelection(selectedText.length());
        var transformer = new PagesTextSelectionTransformer(bodyOnlyPages, this::getRunRenderer);

        var docxTextSelection = transformer.transform(selection);
        assertEquals(List.of(selectedText), renderDocxSelection(docxTextSelection, getRunRenderer(page)));
    }

    @Test
    void returnSelectionAdjustedForFootnotesOnlyPages() {
        int page = 0;
        int line = 2; // Note that if the page only contains content from footnotes, the selectedText below occurs at the third line (header and body lines are not counted)
        String selectedText = "nd line of Footnote #2 h";
        TextSelection selection = new TextCoordinate(page, line, 4).extendToSelection(selectedText.length());
        var transformer = new PagesTextSelectionTransformer(footnotesOnlyPages, this::getRunRenderer);

        var docxTextSelection = transformer.transform(selection);
        assertEquals(List.of(selectedText), renderDocxSelection(docxTextSelection, getRunRenderer(page)));
    }

    private RunRenderer getRunRenderer(int currentPage) {
        return getRunRenderer(currentPage, pages.size());
    }

    // Convenience to enable method-reference use
    private RunRenderer getRunRenderer(int currentPage, int pageCount) {
        return new SimpleRunRenderer(currentPage, pageCount, new SimpleArabicNumberRenderer());

    }

    // Note that all segments of the selection should belong to the same page, otherwise the paragraphRenderer can
    // evaluate special placeholders (e.g. current page) incorrectly.
    // This means that selections that cross page boundaries are not fully supported in the test.
    private List<String> renderDocxSelection(DocxTextSelection<? extends CompositeRun> docxTextSelection, RunRenderer runRenderer) {

        ParagraphRenderer paragraphRenderer = new SimpleParagraphRenderer(runRenderer);
        var paragraphs = docxTextSelection.segment().paragraphs();
        if (paragraphs.isEmpty()) return List.of();
        if (paragraphs.size() == 1)
            return List.of(bothEndsTruncatedRenderer(docxTextSelection, paragraphRenderer).render(paragraphs.getFirst()));

        var paragraphsSplit = new ListEnds<>(paragraphs);
        var result = new ArrayList<String>();
        result.add(startTruncatedRenderer(docxTextSelection, paragraphRenderer).render(paragraphsSplit.first()));
        result.addAll(paragraphsSplit.middle().stream().map(paragraphRenderer::render).toList());
        result.add(endTruncatedRenderer(docxTextSelection, paragraphRenderer).render(paragraphsSplit.last()));

        return result;
    }

    private ParagraphRenderer bothEndsTruncatedRenderer(DocxTextSelection<? extends CompositeRun> docxTextSelection, ParagraphRenderer paragraphRenderer) {
        return new TruncatedParagraphRenderer(paragraphRenderer, docxTextSelection.indexOfFirstCharacter(), docxTextSelection.indexOfLastCharacter());
    }

    private ParagraphRenderer startTruncatedRenderer(DocxTextSelection<? extends CompositeRun> docxTextSelection, ParagraphRenderer paragraphRenderer) {
        return new TruncatedStartParagraphRenderer(paragraphRenderer, docxTextSelection.indexOfFirstCharacter());
    }

    private ParagraphRenderer endTruncatedRenderer(DocxTextSelection<? extends CompositeRun> docxTextSelection, ParagraphRenderer paragraphRenderer) {
        return new TruncatedEndParagraphRenderer(paragraphRenderer, docxTextSelection.indexOfLastCharacter());
    }
}