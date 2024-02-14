package nitpeek.io.docx.internal.pagesource;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import org.docx4j.jaxb.XPathBinderAssociationIsPartialException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.CTFtnEdnRef;
import org.docx4j.wml.P;
import org.docx4j.wml.R;
import org.docx4j.wml.Text;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class DefaultDocxPageExtractor implements DocxPageExtractor {

    private final MainDocumentPart document;
    private final List<DocxPage> pages = new ArrayList<>();

    private final Set<Integer> currentPageFootnotes = new HashSet<>();
    private final List<String> currentPageLines = new ArrayList<>();

    /**
     * @return unmodifiable copy
     */
    @Override
    public List<DocxPage> extractPages() {
        return List.copyOf(pages);
    }

    public DefaultDocxPageExtractor(WordprocessingMLPackage docx) throws JAXBException, XPathBinderAssociationIsPartialException {
        this.document = docx.getMainDocumentPart();
        collectPages();
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
        var currentLine = new StringBuilder();
        for (P p : paragraphs) {
            currentLine = appendParagraph(currentLine, p);
        }

        // Add last page
        if (!currentPageLines.isEmpty()) finishPageAndReturnNext(currentLine);
    }

    private StringBuilder appendParagraph(StringBuilder currentLine, P paragraph) throws JAXBException, XPathBinderAssociationIsPartialException {
        if (isParagraphOnNewPage(paragraph)) {
            currentLine = finishPageAndReturnNext(currentLine);
        }
        currentLine = appendAndSplitIfNecessary(currentLine, paragraph);
        if (!currentLine.isEmpty()) {
            currentPageLines.add(currentLine.toString());
            currentLine = new StringBuilder();
        }

        return currentLine;
    }

    private boolean isParagraphOnNewPage(P paragraph) throws JAXBException, XPathBinderAssociationIsPartialException {
        return !document.getJAXBNodesViaXPath("w:pPr[w:pageBreakBefore]", paragraph, false).isEmpty();
    }

    private StringBuilder finishPageAndReturnNext(StringBuilder line) {

        if (!line.isEmpty()) currentPageLines.add(line.toString());

        pages.add(new DocxPage(currentPageLines, currentPageFootnotes));
        currentPageLines.clear();
        currentPageFootnotes.clear();
        return new StringBuilder();
    }

    private StringBuilder appendAndSplitIfNecessary(StringBuilder currentLine, P currentParagraph) throws JAXBException, XPathBinderAssociationIsPartialException {
        var relevantElements = getRelevantElements(currentParagraph);

        for (var element : relevantElements) {
            var content = element.getValue();

            switch (content) {
                case Text text -> currentLine.append(text.getValue());
                case CTFtnEdnRef footnoteRef -> {
                    currentLine.append(footnoteRef.getId());
                    currentPageFootnotes.add(footnoteRef.getId().intValue());
                }
                case R.LastRenderedPageBreak ignored -> currentLine = finishPageAndReturnNext(currentLine);

                default -> throw new IllegalStateException("Unexpected value: " + content);
            }
        }
        return currentLine;
    }

    private List<JAXBElement<?>> getRelevantElements(P currentParagraph) throws JAXBException, XPathBinderAssociationIsPartialException {
        var relevantNodes = document.getJAXBNodesViaXPath("w:r/*[self::w:t or self::w:footnoteReference or self::w:lastRenderedPageBreak]", currentParagraph, false);
        return JaxbUtil.keepJaxbElements(relevantNodes);
    }
}
