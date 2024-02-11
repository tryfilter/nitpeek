package nitpeek.io.docx.internal;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import org.docx4j.jaxb.XPathBinderAssociationIsPartialException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.openpackaging.parts.relationships.RelationshipsPart;
import org.docx4j.wml.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class DocxPageExtractor {

    private static final String PARAGRAPH_SEPARATOR = "\n";
    private final MainDocumentPart document;
    private final RelationshipsPart relationshipsPart;
    private final List<String> pages = new ArrayList<>();

    private final int pageCount;

    private final Set<Integer> currentPageFootnotes = new HashSet<>();

    /**
     * @return unmodifiable copy
     */
    public List<String> getPages() {
        return List.copyOf(pages);
    }

    public DocxPageExtractor(WordprocessingMLPackage docx) throws JAXBException, XPathBinderAssociationIsPartialException {
        this.document = docx.getMainDocumentPart();
        this.relationshipsPart = document.getRelationshipsPart();
        this.pageCount = computePageCount();
        collectPages();
    }

    private int computePageCount() throws JAXBException, XPathBinderAssociationIsPartialException {
        int renderedPageBreaks = document.getJAXBNodesViaXPath("//w:lastRenderedPageBreak", false).size();
        int forcedPageBreaks = document.getJAXBNodesViaXPath("//w:pageBreakBefore", false).size();
        return renderedPageBreaks + forcedPageBreaks + 1; // one page always exists; every page break adds a page.
    }

    private void collectPages() throws JAXBException, XPathBinderAssociationIsPartialException {
        var paragraphs = getParagraphs();
        collectPagesFromParagraphs(paragraphs);
    }

    private List<P> getParagraphs() throws JAXBException, XPathBinderAssociationIsPartialException {
        var paragraphs = document.getJAXBNodesViaXPath("//w:p", false);
        return JaxbUtil.keepElementsOfType(paragraphs, P.class);
    }

    private void collectPagesFromParagraphs(List<P> paragraphs) throws JAXBException, XPathBinderAssociationIsPartialException {
        var currentPage = new StringBuilder();
        for (P p : paragraphs) {
            currentPage = appendParagraph(currentPage, p);
        }

        // Add last page
        if (!currentPage.isEmpty()) finishPageAndReturnNext(currentPage);
    }

    private StringBuilder appendParagraph(StringBuilder currentPage, P paragraph) throws JAXBException, XPathBinderAssociationIsPartialException {
        if (isParagraphOnNewPage(paragraph)) {
            currentPage = finishPageAndReturnNext(currentPage);
        }
        currentPage = appendAndSplitIfNecessary(currentPage, paragraph);
        appendSeparatorIfNotAlreadyPresent(currentPage);
        return currentPage;
    }

    private boolean isParagraphOnNewPage(P paragraph) throws JAXBException, XPathBinderAssociationIsPartialException {
        return !document.getJAXBNodesViaXPath("w:pPr[w:pageBreakBefore]", paragraph, false).isEmpty();
    }

    private void appendSeparatorIfNotAlreadyPresent(StringBuilder builder) {
        if (builder.lastIndexOf(PARAGRAPH_SEPARATOR) != builder.length() - PARAGRAPH_SEPARATOR.length())
            builder.append(PARAGRAPH_SEPARATOR);
    }

    private StringBuilder finishPageAndReturnNext(StringBuilder page) {
        int expectedSeparatorStart = page.length() - PARAGRAPH_SEPARATOR.length();
        // remove superfluous separator from end of page
        if (page.lastIndexOf(PARAGRAPH_SEPARATOR) == expectedSeparatorStart)
            page.delete(expectedSeparatorStart, page.length());

        var finalPage = withFooterAndFootnotes(withHeader(page));
        pages.add(finalPage.toString());
        currentPageFootnotes.clear();
        return new StringBuilder();
    }

    private StringBuilder withHeader(StringBuilder page) {
        var header = getHeader();
        if (header != null)
            page
                    .insert(0, PARAGRAPH_SEPARATOR) // between header and body
                    .insert(0, rendered(header.getContent()));
        return page;
    }

    private Hdr getHeader() {
        return JaxbUtil.getRelatedObject(relationshipsPart, Hdr.class);
    }

    private StringBuilder withFooterAndFootnotes(StringBuilder page) {
        page = withFootnotes(page);
        var footer = getFooter();
        if (footer != null)
            page
                    .append(PARAGRAPH_SEPARATOR) // between footnotes and footer
                    .append(rendered(footer.getContent()));
        return page;
    }

    private Ftr getFooter() {
        return JaxbUtil.getRelatedObject(relationshipsPart, Ftr.class);
    }

    private StringBuilder withFootnotes(StringBuilder page) {
        var footnotes = getFootnotes();
        if (footnotes == null) return page;

        var relevantFootnotes = footnotes.getFootnote().stream().filter(f -> f.getType() == null && currentPageFootnotes.contains(f.getId().intValue())).toList();
        if (relevantFootnotes.isEmpty()) return page;

        for (var footnote : relevantFootnotes) {
            page.append(PARAGRAPH_SEPARATOR); // between body and footnotes, then between footnotes
            page.append(footnote.getId()).append(' ');
            page.append(rendered(footnote.getContent()));
        }

        return page;
    }

    private CTFootnotes getFootnotes() {
        return JaxbUtil.getRelatedObject(relationshipsPart, CTFootnotes.class);
    }

    private String rendered(List<Object> paragraphs) {
        ParagraphRenderer renderer = new PageInfoRenderer(currentPageNumber(), pageCount);
        var properParagraphs = JaxbUtil.keepElementsOfType(paragraphs, P.class);
        if (properParagraphs.isEmpty()) return "";
        var builder = new StringBuilder(renderer.render(properParagraphs.getFirst()));
        for (var paragraph : properParagraphs.stream().skip(1).toList()) {
            builder.append(PARAGRAPH_SEPARATOR).append(renderer.render(paragraph));
        }
        return builder.toString();
    }

    private int currentPageNumber() {
        return pages.size() + 1;
    }

    private StringBuilder appendAndSplitIfNecessary(StringBuilder currentPage, P currentParagraph) throws JAXBException, XPathBinderAssociationIsPartialException {
        var relevantElements = getRelevantElements(currentParagraph);

        for (var element : relevantElements) {
            var content = element.getValue();

            switch (content) {
                case Text text -> currentPage.append(text.getValue());
                case CTFtnEdnRef footnoteRef -> {
                    currentPage.append(footnoteRef.getId());
                    currentPageFootnotes.add(footnoteRef.getId().intValue());
                }
                case R.LastRenderedPageBreak ignored -> {
                    currentPage = finishPageAndReturnNext(currentPage);
                }

                default -> throw new IllegalStateException("Unexpected value: " + content);
            }
        }
        return currentPage;
    }

    private List<JAXBElement<?>> getRelevantElements(P currentParagraph) throws JAXBException, XPathBinderAssociationIsPartialException {
        var relevantNodes = document.getJAXBNodesViaXPath("w:r/*[self::w:t or self::w:footnoteReference or self::w:lastRenderedPageBreak]", currentParagraph, false);
        return JaxbUtil.keepJaxbElements(relevantNodes);
    }
}
