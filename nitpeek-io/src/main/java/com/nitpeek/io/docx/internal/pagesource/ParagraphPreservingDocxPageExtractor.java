package com.nitpeek.io.docx.internal.pagesource;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import com.nitpeek.core.api.report.ReportingException;
import com.nitpeek.io.docx.internal.common.DocxUtil;
import com.nitpeek.io.docx.internal.common.SimpleDocxSegment;
import com.nitpeek.io.docx.internal.pagesource.render.SimpleArabicNumberRenderer;
import com.nitpeek.io.docx.internal.pagesource.render.SimpleParagraphRenderer;
import com.nitpeek.io.docx.internal.pagesource.render.SimpleRunRenderer;
import com.nitpeek.io.docx.internal.reporter.SegmentedDocxPage;
import com.nitpeek.io.docx.types.CompositeRun;
import com.nitpeek.io.docx.types.DocxPage;
import com.nitpeek.io.docx.types.DocxSegment;
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
        var paragraphObjects = document.getJAXBNodesViaXPath("//w:p", false);
        return DocxUtil.getAllParagraphs(paragraphObjects);
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
        processParagraph(paragraph);
    }

    private boolean isParagraphOnNewPage(P paragraph) throws JAXBException, XPathBinderAssociationIsPartialException {
        return !document.getJAXBNodesViaXPath("w:pPr[w:pageBreakBefore]", paragraph, false).isEmpty();
    }

    private void processParagraph(P currentParagraph) {

        boolean paragraphAdded = false;
        boolean pageIsOver = false;
        var runs = DocxUtil.getRuns(currentParagraph);
        for (int i = 0; i < runs.size(); i++) {
            var run = runs.get(i);
            saveFootnotes(run);
            if (isRunOnNextPage(run)) {
                pageIsOver = true;
                paragraphAdded = addParagraphIfNotEmptyUpTo(i, currentParagraph);
            }
        }

        if (!paragraphAdded) addParagraphIfNotEmptyUpTo(runs.size(), currentParagraph);
        if (pageIsOver) finishCurrentPage();
    }

    /**
     * Because paragraphs are equivalent to lines in {@code TextCoordinate} terms, only consider a paragraph to be part
     * of the current page if any of the runs belonging to the current page have visible contents.
     * Otherwise, the row count of the body gets inflated from a paragraph that is not actually visible on the page.
     */
    private boolean addParagraphIfNotEmptyUpTo(int firstExcludedRun, P paragraph) {
        // We don't care about the actual page coordinates, we just want to check if something at all would be rendered
        var simpleRenderer = new SimpleParagraphRenderer(new SimpleRunRenderer(0, 0, new SimpleArabicNumberRenderer()));

        int lastIncludedRun = firstExcludedRun - 1;
        var paragraphPart = paragraphTransformer.transform(paragraph).partitionTo(lastIncludedRun);
        // If up to the specified run nothing would get rendered, consider the paragraph empty and don't add it to the
        // current page
        if (simpleRenderer.render(paragraphPart).isEmpty()) return false;

        currentPageParagraphs.add(paragraph);
        return true;
    }


    private void finishCurrentPage() {

        int currentPage = currentPageNumber();

        var header = new ComponentSegmentExtractor<>(docx, Hdr.class, currentPage, paragraphTransformer).extractSegment().orElse(null);
        var footer = new ComponentSegmentExtractor<>(docx, Ftr.class, currentPage, paragraphTransformer).extractSegment().orElse(null);

        var body = assembleBodySegment(currentPageParagraphs);
        var footnotes = computeFootnotes();
        var preProcessedPage = pageTransformer.apply(new SimpleDocxPage<>(header, body, footnotes, footer));
        pages.add(new DefaultDocxPage<>(preProcessedPage));
        resetPageState();
    }

    private DocxSegment<CompositeRun> assembleBodySegment(List<P> paragraphs) {
        return new SimpleDocxSegment<>(paragraphs.stream().map(paragraphTransformer::transform).toList());
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