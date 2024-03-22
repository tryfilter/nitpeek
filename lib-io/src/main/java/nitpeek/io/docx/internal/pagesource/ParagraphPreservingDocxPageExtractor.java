package nitpeek.io.docx.internal.pagesource;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import nitpeek.core.api.report.ReportingException;
import nitpeek.io.docx.internal.common.DocxUtil;
import nitpeek.io.docx.internal.common.SimpleDocxSegment;
import nitpeek.io.docx.internal.pagesource.render.SimpleArabicNumberRenderer;
import nitpeek.io.docx.internal.pagesource.render.SimpleParagraphRenderer;
import nitpeek.io.docx.internal.pagesource.render.SimpleRunRenderer;
import nitpeek.io.docx.internal.reporter.SegmentedDocxPage;
import nitpeek.io.docx.types.CompositeRun;
import nitpeek.io.docx.types.DocxPage;
import nitpeek.io.docx.types.DocxParagraph;
import nitpeek.io.docx.types.DocxSegment;
import nitpeek.util.collection.ListEnds;
import org.docx4j.jaxb.XPathBinderAssociationIsPartialException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.*;

import java.math.BigInteger;
import java.util.*;
import java.util.function.UnaryOperator;

public final class ParagraphPreservingDocxPageExtractor implements DocxPageExtractor {

    private final MainDocumentPart document;
    private final WordprocessingMLPackage docx;

    private final Set<Integer> currentPageFootnotes = new HashSet<>();
    private final List<P> currentPageParagraphs = new ArrayList<>();

    private final List<SegmentedDocxPage<CompositeRun>> pages = new ArrayList<>();

    private final ParagraphTransformer paragraphTransformer = new CompositingParagraphTransformer(new ComplexRunsGrouper());

    private final UnaryOperator<DocxPage<CompositeRun>> pageTransformer;

    @Override
    public List<SegmentedDocxPage<CompositeRun>> extractPages() {
        return List.copyOf(pages);
    }

    public ParagraphPreservingDocxPageExtractor(WordprocessingMLPackage docx, UnaryOperator<DocxPage<CompositeRun>> pageTransformer) throws ReportingException {
        this.docx = docx;
        this.document = docx.getMainDocumentPart();
        this.pageTransformer = pageTransformer;
        try {
            collectPages();
        } catch (JAXBException | XPathBinderAssociationIsPartialException e) {
            throw new ReportingException("Unable to extract pages from DOCX", e);
        }
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
            saveFootnotes(run);
            if (isRunOnNextPage(run)) {
                startNewPageAtRun(i, currentParagraph);
                return;
            }
        }
        addParagraphIfNotEmptyUpTo(runs.size(), currentParagraph);
    }

    private void startNewPageAtRun(int runIndex, P paragraph) {
        addParagraphIfNotEmptyUpTo(runIndex, paragraph);
        finishCurrentPage();
    }

    /**
     * Because paragraphs are equivalent to lines in {@code TextCoordinate} terms, only consider a paragraph to be part
     * of the current page if any of the runs belonging to the current page have visible contents.
     * Otherwise, the row count of the body gets inflated from a paragraph that is not actually visible on the page.
     */
    private void addParagraphIfNotEmptyUpTo(int firstExcludedRun, P paragraph) {
        // We don't care about the actual page coordinates, we just want to check if something at all would be rendered
        var simpleRenderer = new SimpleParagraphRenderer(new SimpleRunRenderer(0, 0, new SimpleArabicNumberRenderer()));
        // If up to the specified run nothing would get rendered, consider the paragraph empty and don't add it to the
        // current page
        int lastIncludedRun = firstExcludedRun - 1;
        var paragraphPart = paragraphTransformer.transform(paragraph).partitionTo(lastIncludedRun);
        if (simpleRenderer.render(paragraphPart).isEmpty()) return;

        currentPageParagraphs.add(paragraph);
    }


    private void finishCurrentPage() {

        int currentPage = currentPageNumber();

        var header = new ComponentSegmentExtractor<>(docx, Hdr.class, currentPage, paragraphTransformer).extractSegment().orElse(null);
        var footer = new ComponentSegmentExtractor<>(docx, Ftr.class, currentPage, paragraphTransformer).extractSegment().orElse(null);

        var lastParagraph = currentPageParagraphs.reversed().stream().findFirst();
        var body = assembleBodySegment(currentPageParagraphs, lastParagraph.map(p -> DocxUtil.getRuns(p).size() - 1).orElse(0));
        var footnotes = computeFootnotes();
        var preProcessedPage = pageTransformer.apply(new SimpleDocxPage<>(header, body, footnotes, footer));
        pages.add(new DefaultDocxPage<>(preProcessedPage));
        resetPageState();
    }

    private DocxSegment<CompositeRun> assembleBodySegment(List<P> paragraphs, int indexOfLastRun) {
        if (paragraphs.isEmpty()) return new SimpleDocxSegment<>(List.of());
        if (paragraphs.size() == 1)
            return new SimpleDocxSegment<>(List.of(paragraphTransformer.transformBetween(0, indexOfLastRun, paragraphs.getFirst())));

        var paragraphsSplit = new ListEnds<>(paragraphs);
        var resultingParagraphs = new ArrayList<DocxParagraph<CompositeRun>>(paragraphs.size());
        resultingParagraphs.add(paragraphTransformer.transformFrom(0, paragraphsSplit.first()));
        resultingParagraphs.addAll(paragraphsSplit.middle().stream().map(paragraphTransformer::transform).toList());
        resultingParagraphs.add(paragraphTransformer.transformTo(indexOfLastRun, paragraphsSplit.last()));
        return new SimpleDocxSegment<>(resultingParagraphs);
    }

    private int currentPageNumber() {
        return pages.size();
    }

    private void resetPageState() {
        currentPageParagraphs.clear();
        currentPageFootnotes.clear();
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