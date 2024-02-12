package nitpeek.io.docx.internal.render;

import nitpeek.core.api.common.TextPage;
import nitpeek.core.impl.analyze.SimpleTextPage;
import nitpeek.io.docx.internal.DocxPage;
import nitpeek.io.docx.internal.JaxbUtil;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public final class HeaderFooterFootnotesDocxPageRenderer implements DocxPageRenderer {

    private final WordprocessingMLPackage docx;

    private List<DocxPage> pages = List.of();

    public HeaderFooterFootnotesDocxPageRenderer(WordprocessingMLPackage docx) {
        this.docx = docx;
    }

    @Override
    public List<TextPage> renderPages(List<DocxPage> pages) {
        this.pages = List.copyOf(pages);
        var result = new ArrayList<TextPage>();

        for (int i = 0; i < pages.size(); i++) {
            result.add(renderPage(pages.get(i), i));
        }

        return result;
    }

    private TextPage renderPage(DocxPage page, int pageIndex) {
        var header = renderHeader(pageIndex);
        var footer = renderFooter(pageIndex);
        var footnotes = renderFootnotes(pageIndex);
        var fullPage = Stream.of(header, page.body(), footnotes, footer).flatMap(Collection::stream).toList();

        return new SimpleTextPage(fullPage, pageIndex);
    }

    private List<String> renderHeader(int pageIndex) {
        return getRenderer(pageIndex, Hdr.class).renderParagraphs();
    }

    private List<String> renderFooter(int pageIndex) {
        return getRenderer(pageIndex, Ftr.class).renderParagraphs();
    }

    private <T extends ContentAccessor> GenericRenderer getRenderer(int pageIndex, Class<T> componentClass) {
        return new SimpleComponentRenderer<>(docx, componentClass, new SimpleContentRenderer(getParagraphRenderer(pageIndex)));
    }

    private List<String> renderFootnotes(int pageIndex) {
        return getFootnotesRenderer(pageIndex).renderParagraphs();
    }

    private GenericRenderer getFootnotesRenderer(int pageIndex) {
        var footnotes = getFootnotes();
        var currentPage = pages.get(pageIndex);

        return () -> new DefaultFootnotesRenderer(getParagraphRenderer(pageIndex), footnotes).renderFootnotes(currentPage.footnotes());
    }

    private CTFootnotes getFootnotes() {
        return JaxbUtil.getRelatedObject(docx.getMainDocumentPart().getRelationshipsPart(), CTFootnotes.class);
    }

    private ParagraphRenderer getParagraphRenderer(int pageIndex) {
        return new PageInfoParagraphRenderer(
                pageIndex + 1, // pageIndex is 0-based
                pages.size()
        );
    }
}
