package nitpeek.io.docx.internal.reporter;

import nitpeek.core.api.common.TextCoordinate;
import nitpeek.core.api.common.TextSelection;
import nitpeek.io.docx.internal.common.*;
import nitpeek.io.docx.internal.common.Partitioned;
import nitpeek.io.docx.internal.reporter.EdgeDetector.SelectionEdge;
import nitpeek.io.docx.internal.common.RunRenderer;
import nitpeek.io.docx.types.CompositeRun;
import nitpeek.io.docx.types.DocxParagraph;
import nitpeek.io.docx.types.DocxSegment;
import nitpeek.io.docx.types.SplittableRun;
import nitpeek.io.docx.internal.common.DocxTextSelection;
import nitpeek.util.collection.ListEnds;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Stream;

/**
 * This is a simplified implementation which assumes that footnotes are at the end of the page, after the body but
 * before the footer.<br>
 * The {@code DocxTextSelection}s produced by this transformer are already splittable.
 * <br>
 * Note that for DOCX documents, cross-page selections are subtle: they only contain the contents of the body of each
 * page, skipping any footnotes and footer/header text in between.<br>
 * While this selection transformer abstracts this fact away (placing in multipage selections the text as it appears in
 * reading order, including any header, footer, or footnote text that entails), highlighting the resulting
 * {@code DocxTextSelection} in an actual DOCX document may be problematic.<br>
 * <br>
 * Note that this transformer is not section-aware, i.e. it doesn't respect multiple header/footer definitions.
 */
public final class PagesTextSelectionTransformer implements TextSelectionTransformer<SplittableRun> {

    private final List<Partitioned<? extends DocxSegment<? extends CompositeRun>>> pages;
    private final BiFunction<Integer, Integer, RunRenderer> runRendererFactory;

    private final EdgeDetector edgeDetector = new EdgeDetector();
    private final SplitEnabler splitEnabler;

    public PagesTextSelectionTransformer(List<? extends Partitioned<? extends DocxSegment<? extends CompositeRun>>> pages, BiFunction<Integer, Integer, RunRenderer> runRendererFactory) {
        this.pages = List.copyOf(pages);
        this.runRendererFactory = runRendererFactory;
        this.splitEnabler = new SplitEnabler(new DefaultRunSplitEnabler());
    }

    @Override
    public DocxTextSelection<SplittableRun> transform(TextSelection textSelection) {

        if (pages.isEmpty()) return SimpleDocxTextSelection.empty();
        var paragraphs = computeParagraphs(textSelection);
        return createSelection(paragraphs, textSelection);
    }

    private List<DocxParagraph<SplittableRun>> computeParagraphs(TextSelection textSelection) {
        var selectedPages = pages.subList(textSelection.fromInclusive().page(), textSelection.toInclusive().page() + 1);
        if (selectedPages.isEmpty()) return List.of();
        if (selectedPages.size() == 1) return computeParagraphsFromSinglePage(textSelection, selectedPages.getFirst());

        return computeParagraphsFromMultiplePages(textSelection, selectedPages);
    }

    private List<DocxParagraph<SplittableRun>> computeParagraphsFromSinglePage(TextSelection textSelection, Partitioned<? extends DocxSegment<? extends CompositeRun>> page) {
        var firstPageParagraphs = page.partitionBetween(textSelection.fromInclusive().line(), textSelection.toInclusive().line());
        return transformPage(firstPageParagraphs, 0);
    }

    private List<DocxParagraph<SplittableRun>> computeParagraphsFromMultiplePages(TextSelection textSelection, List<Partitioned<? extends DocxSegment<? extends CompositeRun>>> pages) {
        var splittableFirstPageParagraphs = transformPage(pages.getFirst().partitionFrom(textSelection.fromInclusive().line()), 0);
        var splittableMiddlePageParagraphs = new ArrayList<DocxParagraph<SplittableRun>>();
        for (int i = 1; i < pages.size() - 1; i++) {
            var page = pages.get(i);
            splittableMiddlePageParagraphs.addAll(transformPage(page.fullPartition(), i));
        }
        int lastPageIndex = pages.size() - 1;
        var splittableLastPageParagraphs = transformPage(pages.getLast().partitionTo(textSelection.toInclusive().line()), lastPageIndex);

        return Stream.of(splittableFirstPageParagraphs, splittableMiddlePageParagraphs, splittableLastPageParagraphs).flatMap(Collection::stream).toList();
    }

    private List<DocxParagraph<SplittableRun>> transformPage(DocxSegment<? extends CompositeRun> pageContent, int pageIndex) {
        var runRenderer = runRendererFactory.apply(pageIndex, pages.size());
        return pageContent.paragraphs().stream().map(paragraph -> splitEnabler.convertParagraph(paragraph, runRenderer)).toList();
    }

    private DocxTextSelection<SplittableRun> createSelection(List<DocxParagraph<SplittableRun>> paragraphs, TextSelection textSelection) {
        var first = computeFirstCharacterInFirstRun(textSelection);
        var last = computeLastCharacterInLastRun(textSelection);
        if (paragraphs.isEmpty()) return SimpleDocxTextSelection.empty();
        if (paragraphs.size() == 1) return singleParagraphSelection(paragraphs.getFirst(), first, last);

        var paragraphsSplit = new ListEnds<>(paragraphs);

        var firstParagraphTransformed = paragraphsSplit.first().partitionFrom(first.runIndex());
        var middleParagraphs = paragraphsSplit.middle();
        var lastParagraphTransformed = paragraphsSplit.last().partitionTo(last.runIndex());

        var transformedParagraphs = Stream.of(
                List.of(firstParagraphTransformed),
                middleParagraphs,
                List.of(lastParagraphTransformed)
        ).flatMap(Collection::stream).toList();

        return selection(transformedParagraphs, first.characterIndexWithinRun(), last.characterIndexWithinRun());
    }

    private DocxTextSelection<SplittableRun> singleParagraphSelection(DocxParagraph<SplittableRun> paragraph, SelectionEdge first, SelectionEdge last) {
        var subParagraph = paragraph.partitionBetween(first.runIndex(), last.runIndex());
        return selection(List.of(subParagraph), first.characterIndexWithinRun(), last.characterIndexWithinRun());
    }

    DocxTextSelection<SplittableRun> selection(List<DocxParagraph<SplittableRun>> paragraphs, int firstCharacter, int lastCharacter) {
        return new SimpleDocxTextSelection<>(new SimpleDocxSegment<>(paragraphs), firstCharacter, lastCharacter);
    }

    private SelectionEdge computeFirstCharacterInFirstRun(TextSelection textSelection) {
        return computeCharacterAndRunCorrespondingTo(textSelection.fromInclusive());
    }

    private SelectionEdge computeLastCharacterInLastRun(TextSelection textSelection) {
        return computeCharacterAndRunCorrespondingTo(textSelection.toInclusive());
    }

    private SelectionEdge computeCharacterAndRunCorrespondingTo(TextCoordinate textCoordinate) {
        int pageIndex = textCoordinate.page();
        var page = pages.get(pageIndex);
        int lineIndex = textCoordinate.line();
        var lineParagraph = page.partitionBetween(lineIndex, lineIndex).paragraphs().getFirst();
        int characterIndex = textCoordinate.character();
        var runRenderer = runRendererFactory.apply(pageIndex, pages.size());

        var runs = lineParagraph.runs();
        return edgeDetector.computeSelectionEdge(runs, characterIndex, runRenderer);
    }
}
