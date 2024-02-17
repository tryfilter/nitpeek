package nitpeek.io.docx.internal.pagesource;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import org.docx4j.jaxb.XPathBinderAssociationIsPartialException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.*;

import java.math.BigInteger;
import java.util.*;

public final class DefaultDocxPageExtractor implements DocxPageExtractor {

    private final MainDocumentPart document;
    private final WordprocessingMLPackage docx;

    private final Set<Integer> currentPageFootnotes = new HashSet<>();
    private final List<P> currentPageParagraphs = new ArrayList<>();
    private int firstRunCurrentParagraph = 0;

    private final List<DocxPage> pages = new ArrayList<>();

    /**
     * @return an unmodifiable copy
     */
    @Override
    public List<DocxPage> extractPages() {
        return List.copyOf(pages);
    }

    public DefaultDocxPageExtractor(WordprocessingMLPackage docx) throws JAXBException, XPathBinderAssociationIsPartialException {
        this.docx = docx;
        this.document = docx.getMainDocumentPart();
        collectPages();
    }

    private void collectPages() throws JAXBException, XPathBinderAssociationIsPartialException {
        var paragraphs = getBodyParagraphs();
        collectPagesFromParagraphs(paragraphs);
    }

    private List<P> getBodyParagraphs() throws JAXBException, XPathBinderAssociationIsPartialException {
        var paragraphs = document.getJAXBNodesViaXPath("//w:p", false);
        return DocxUtil.keepElementsOfType(paragraphs, P.class);
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
                // The current paragraph is split between pages: add it to the current page too
                currentPageParagraphs.add(currentParagraph);
                // The current run starts on a new page. The previous run is the last run that belongs to the current page.
                finishCurrentPageAndSplitBefore(i);
            }
        }
        currentPageParagraphs.add(currentParagraph);
    }

    private void finishCurrentPage() {

        var previousParagraph = currentPageParagraphs.reversed().stream().findFirst();
        var runCount = previousParagraph.map(p -> DocxUtil.getRuns(p).size()).orElse(0);
        finishCurrentPageAndSplitBefore(runCount);
        // The current page is being finished at a paragraph border, therefore the split needs to be undone.
        firstRunCurrentParagraph = 0;
    }

    private void finishCurrentPageAndSplitBefore(int splitIndex) {

        int currentPage = currentPageNumber();

        var header = new ComponentSegmentExtractor<>(docx, Hdr.class, currentPage).extractSegment();
        var footer = new ComponentSegmentExtractor<>(docx, Ftr.class, currentPage).extractSegment();
        int lastIncludedIndex = splitIndex - 1;
        var body = new DocxSegment(currentPageParagraphs, firstRunCurrentParagraph, lastIncludedIndex);
        var footnotes = computeFootnotes();
        pages.add(new DocxPage(currentPage, header, body, footnotes, footer));
        resetPageState(splitIndex);
    }

    private int currentPageNumber() {
        return pages.size();
    }

    private void resetPageState(int splitIndex) {
        currentPageParagraphs.clear();
        currentPageFootnotes.clear();
        firstRunCurrentParagraph = splitIndex;
    }

    private Map<Integer, DocxSegment> computeFootnotes() {
        var result = new HashMap<Integer, DocxSegment>();
        for (int footnoteRef : currentPageFootnotes) {
            var footnote = new FootnotesSegmentExtractor(docx, footnoteRef).extractSegment();
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
