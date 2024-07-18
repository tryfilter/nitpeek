package com.nitpeek.io.docx.internal.pagesource;

import com.nitpeek.core.api.report.ReportingException;
import com.nitpeek.io.docx.internal.common.DocxUtil;
import com.nitpeek.io.docx.internal.pagesource.render.SimpleArabicNumberRenderer;
import com.nitpeek.io.docx.internal.pagesource.render.SimpleParagraphRenderer;
import com.nitpeek.io.docx.internal.pagesource.render.SimpleRunRenderer;
import com.nitpeek.io.docx.internal.reporter.SegmentedDocxPage;
import com.nitpeek.io.docx.types.CompositeRun;
import com.nitpeek.io.docx.types.DocxPage;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import org.docx4j.jaxb.XPathBinderAssociationIsPartialException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.CTFtnEdnRef;
import org.docx4j.wml.P;
import org.docx4j.wml.R;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

public final class ParagraphPreservingDocxPageExtractor implements DocxPageExtractor {

    private final MainDocumentPart document;
    private final WordprocessingMLPackage docx;

    private final List<PageBuilder> pages = new ArrayList<>();

    private final ParagraphTransformer paragraphTransformer = new CompositingParagraphTransformer(new ComplexRunsGrouper());

    private final UnaryOperator<DocxPage<CompositeRun>> pageTransformer;

    private PageBuilder currentPage;
    @Override
    public List<SegmentedDocxPage<CompositeRun>> extractPages() {
        return List.copyOf(pages.stream()
                .map(pageBuilder -> pageTransformer.apply(pageBuilder.build()))
                .map(DefaultDocxPage::new)
                .toList());
    }

    public ParagraphPreservingDocxPageExtractor(WordprocessingMLPackage docx, UnaryOperator<DocxPage<CompositeRun>> pageTransformer) throws ReportingException {
        this.docx = docx;
        this.document = docx.getMainDocumentPart();
        this.pageTransformer = pageTransformer;
        this.currentPage = new PageBuilder(docx, paragraphTransformer, 0);
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
        if (currentPage.hasBodyContent()) startNextPage();
    }

    private void appendParagraph(P paragraph) throws JAXBException, XPathBinderAssociationIsPartialException {
        if (isParagraphOnNewPage(paragraph)) startNextPage();
        processParagraph(paragraph);
    }

    private boolean isParagraphOnNewPage(P paragraph) throws JAXBException, XPathBinderAssociationIsPartialException {
        return !document.getJAXBNodesViaXPath("w:pPr[w:pageBreakBefore]", paragraph, false).isEmpty();
    }

    private void processParagraph(P currentParagraph) {

        addParagraphIfNotEmpty(currentParagraph);

        for (R run : DocxUtil.getRuns(currentParagraph)) {
            saveFootnotes(run, currentPage);
            if (isRunOnNextPage(run)) startNextPage();
        }

    }


    /**
     * Because paragraphs are equivalent to lines in {@code TextCoordinate} terms, only consider a paragraph to be part
     * of the current page if any of the runs belonging to the current page have visible contents.
     * Otherwise, the row count of the body gets inflated from a paragraph that is not actually visible on the page.
     */
    private void addParagraphIfNotEmpty(P p) {
        // We don't care about the actual page coordinates, we just want to check if something at all would be rendered
        var simpleRenderer = new SimpleParagraphRenderer(new SimpleRunRenderer(0, 0, new SimpleArabicNumberRenderer()));

        var paragraph = paragraphTransformer.transform(p);
        if (simpleRenderer.render(paragraph).isEmpty()) return;

        currentPage.addBodyParagraph(p);
    }

    private void startNextPage() {
        pages.add(currentPage);
        currentPage = new PageBuilder(docx, paragraphTransformer, pages.size());
    }

    private void saveFootnotes(R run, PageBuilder pageBuilder) {
        var footnoteReferences = DocxUtil.keepElementsOfType(getChildren(run), CTFtnEdnRef.class);
        pageBuilder.addFootnoteReferences(footnoteReferences.stream().map(CTFtnEdnRef::getId).map(BigInteger::intValue).toList());
    }

    private boolean isRunOnNextPage(R run) {
        var pageBreaks = DocxUtil.keepElementsOfType(getChildren(run), R.LastRenderedPageBreak.class);
        return !pageBreaks.isEmpty();
    }

    private List<?> getChildren(R run) {
        return DocxUtil.keepJaxbElements(run.getContent()).stream().map(JAXBElement::getValue).toList();
    }
}