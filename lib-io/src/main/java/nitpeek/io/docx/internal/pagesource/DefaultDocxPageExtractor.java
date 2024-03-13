package nitpeek.io.docx.internal.pagesource;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import nitpeek.io.docx.internal.common.DocxParagraph;
import nitpeek.io.docx.internal.common.DocxSegment;
import nitpeek.io.docx.internal.common.DocxUtil;
import nitpeek.io.docx.internal.common.SimpleDocxSegment;
import nitpeek.io.docx.internal.pagesource.render.SimpleArabicNumberRenderer;
import nitpeek.io.docx.internal.pagesource.render.SimpleParagraphRenderer;
import nitpeek.io.docx.internal.pagesource.render.SimpleRunRenderer;
import nitpeek.io.docx.render.CompositeRun;
import nitpeek.io.docx.render.DocxPage;
import nitpeek.util.collection.ListEnds;
import org.docx4j.jaxb.XPathBinderAssociationIsPartialException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.*;

import java.math.BigInteger;
import java.util.*;
import java.util.function.UnaryOperator;

public final class DefaultDocxPageExtractor implements DocxPageExtractor {

    private final MainDocumentPart document;
    private final WordprocessingMLPackage docx;

    private final Set<Integer> currentPageFootnotes = new HashSet<>();
    private final List<P> currentPageParagraphs = new ArrayList<>();
    private int firstRunOfFirstParagraphInCurrentPage = 0;

    private final List<SegmentedDocxPage<CompositeRun>> pages = new ArrayList<>();

    private final ParagraphTransformer paragraphTransformer = new CompositingParagraphTransformer(new ComplexRunsGrouper());

    private final UnaryOperator<DocxPage<CompositeRun>> pageTransformer;

    /**
     * @return an unmodifiable copy
     */
    @Override
    public List<SegmentedDocxPage<CompositeRun>> extractPages() {
        return List.copyOf(pages);
    }

    public DefaultDocxPageExtractor(WordprocessingMLPackage docx, UnaryOperator<DocxPage<CompositeRun>> pageTransformer) throws JAXBException, XPathBinderAssociationIsPartialException {
        this.docx = docx;
        this.document = docx.getMainDocumentPart();
        this.pageTransformer = pageTransformer;
        collectPages();
    }

    private void collectPages() throws JAXBException, XPathBinderAssociationIsPartialException {
        var paragraphs = getBodyParagraphs();
        collectPagesFromParagraphs(paragraphs);
    }

    private List<P> getBodyParagraphs() throws JAXBException, XPathBinderAssociationIsPartialException {
        var paragraphs = document.getJAXBNodesViaXPath("//w:p", false);
        return DocxUtil.getAllParagraphs(paragraphs);
    }

    private void collectPagesFromParagraphs(List<P> paragraphs) throws JAXBException, XPathBinderAssociationIsPartialException {
        for (P p : paragraphs) {
            appendParagraph(p);
        }

        // Add last page
        if (!currentPageParagraphs.isEmpty()) finishCurrentPage();
    }

    private void appendParagraph(P paragraph) throws JAXBException, XPathBinderAssociationIsPartialException {
        if (isParagraphOnNewPage(paragraph)) finishCurrentPage();
        processAndSplitIfNecessary(paragraph);
    }

    private boolean isParagraphOnNewPage(P paragraph) throws JAXBException, XPathBinderAssociationIsPartialException {
        return !document.getJAXBNodesViaXPath("w:pPr[w:pageBreakBefore]", paragraph, false).isEmpty();
    }

    private void processAndSplitIfNecessary(P currentParagraph) {

        var runs = DocxUtil.getRuns(currentParagraph);
        for (int i = 0; i < runs.size(); i++) {
            var run = runs.get(i);
            if (isRunOnNextPage(run)) startNewPageAtRun(i, currentParagraph);
            saveFootnotes(run);
        }
        addParagraphIfNotEmptyUpTo(runs.size(), currentParagraph);
    }

    private void startNewPageAtRun(int runIndex, P paragraph) {
        if (addParagraphIfNotEmptyUpTo(runIndex, paragraph))
            // If the paragraph had renderable contents up to the split index, it has to be split
            finishCurrentPageAndSplitBefore(runIndex);
        else
            // Otherwise consider the entire paragraph part of the new page
            finishCurrentPage();
    }

    /**
     * Because paragraphs are equivalent to lines in {@code TextCoordinate} terms, only consider a paragraph to be part
     * of the current page if any of the runs belonging to the current page have visible contents.
     * Otherwise, the row count of the body gets inflated from a paragraph that is not actually visible on the page.
     *
     * @return true if the paragraph was added (and thus was non-empty), false otherwise.
     */
    private boolean addParagraphIfNotEmptyUpTo(int firstExcludedRun, P paragraph) {
        // We don't care about the actual page coordinates, we just want to check if something at all would be rendered
        var simpleRenderer = new SimpleParagraphRenderer(new SimpleRunRenderer(0, 0, new SimpleArabicNumberRenderer()));
        // If up to the specified run nothing would get rendered, consider the paragraph empty and don't add it to the
        // current page
        int lastIncludedRun = firstExcludedRun - 1;
        var paragraphPart = paragraphTransformer.transform(paragraph).partitionTo(lastIncludedRun);
        if (simpleRenderer.render(paragraphPart).isEmpty()) return false;

        currentPageParagraphs.add(paragraph);
        return true;
    }

    private void finishCurrentPage() {

        var previousParagraph = currentPageParagraphs.reversed().stream().findFirst();
        var runCount = previousParagraph.map(p -> DocxUtil.getRuns(p).size()).orElse(0);
        finishCurrentPageAndSplitBefore(runCount);
        // The current page is being finished at a paragraph border, therefore the split needs to be undone.
        firstRunOfFirstParagraphInCurrentPage = 0;
    }

    private void finishCurrentPageAndSplitBefore(int splitIndex) {

        int currentPage = currentPageNumber();

        var header = new ComponentSegmentExtractor<>(docx, Hdr.class, currentPage, paragraphTransformer).extractSegment().orElse(null);
        var footer = new ComponentSegmentExtractor<>(docx, Ftr.class, currentPage, paragraphTransformer).extractSegment().orElse(null);
        int lastIncludedIndex = splitIndex - 1;

        var body = assembleBodySegment(currentPageParagraphs, firstRunOfFirstParagraphInCurrentPage, lastIncludedIndex);
        var footnotes = computeFootnotes();
        var preProcessedPage = pageTransformer.apply(new SimpleDocxPage<>(header, body, footnotes, footer));
        pages.add(new DefaultDocxPage<>(preProcessedPage));
        resetPageState(splitIndex);
    }

    private DocxSegment<CompositeRun> assembleBodySegment(List<P> paragraphs, int indexOfFirstRun, int indexOfLastRun) {
        if (paragraphs.isEmpty()) return new SimpleDocxSegment<>(List.of());
        if (paragraphs.size() == 1)
            return new SimpleDocxSegment<>(List.of(paragraphTransformer.transformBetween(indexOfFirstRun, indexOfLastRun, paragraphs.getFirst())));

        var paragraphsSplit = new ListEnds<>(paragraphs);
        var resultingParagraphs = new ArrayList<DocxParagraph<CompositeRun>>(paragraphs.size());
        resultingParagraphs.add(paragraphTransformer.transformFrom(indexOfFirstRun, paragraphsSplit.first()));
        resultingParagraphs.addAll(paragraphsSplit.middle().stream().map(paragraphTransformer::transform).toList());
        resultingParagraphs.add(paragraphTransformer.transformTo(indexOfLastRun, paragraphsSplit.last()));
        return new SimpleDocxSegment<>(resultingParagraphs);
    }

    private int currentPageNumber() {
        return pages.size();
    }

    private void resetPageState(int splitIndex) {
        currentPageParagraphs.clear();
        currentPageFootnotes.clear();
        firstRunOfFirstParagraphInCurrentPage = splitIndex;
    }

    private Map<Integer, DocxSegment<CompositeRun>> computeFootnotes() {
        var result = new HashMap<Integer, DocxSegment<CompositeRun>>();
        for (int footnoteRef : currentPageFootnotes) {
            var footnote = new FootnotesSegmentExtractor(docx, footnoteRef, paragraphTransformer).extractSegment();
            footnote.ifPresent(f -> result.put(footnoteRef, f));
        }

        return result;
    }

    private void saveFootnotes(R run) {
        var footnoteReferences = DocxUtil.keepElementsOfType(getChildren(run), CTFtnEdnRef.class);
        currentPageFootnotes.addAll(footnoteReferences.stream().map(CTFtnEdnRef::getId).map(BigInteger::intValue).toList());
    }

    private boolean isRunOnNextPage(R run) {
        var pageBreaks = DocxUtil.keepElementsOfType(getChildren(run), R.LastRenderedPageBreak.class);
        return !pageBreaks.isEmpty();
    }

    private List<?> getChildren(R run) {
        return DocxUtil.keepJaxbElements(run.getContent()).stream().map(JAXBElement::getValue).toList();
    }
}
