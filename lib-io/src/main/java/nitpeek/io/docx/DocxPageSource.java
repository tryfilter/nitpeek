package nitpeek.io.docx;

import jakarta.xml.bind.JAXBException;
import nitpeek.core.api.common.TextPage;
import nitpeek.core.api.process.PageConsumer;
import nitpeek.core.api.process.PageSource;
import nitpeek.core.impl.process.SimplePageSource;
import nitpeek.io.docx.internal.pagesource.DefaultDocxPageExtractor;
import nitpeek.io.docx.internal.pagesource.render.DefaultDocxPageRenderer;
import nitpeek.io.docx.types.CompositeRun;
import nitpeek.io.docx.types.DocxPage;
import org.docx4j.jaxb.XPathBinderAssociationIsPartialException;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

import java.io.InputStream;
import java.util.List;
import java.util.function.UnaryOperator;


public final class DocxPageSource implements PageSource {
    private final PageSource memoryPageSource;

    DocxPageSource(PageSource memoryPageSource) {
        this.memoryPageSource = memoryPageSource;
    }

    /**
     * Note that DOCX documents are not aware of the concept of lines. This means that the lines of the {@code TextPage}s
     * produced by this page source have no direct relation to the lines that are visually apparent when the source DOCX
     * is rendered.
     *
     * @param input representing a valid DOCX file. The InputStream is not closed by this method.
     * @param pageTransformer the transformer to use before reading the page contents
     * @return a page source containing all pages of the provided DOCX
     */
    public static PageSource createFrom(InputStream input, UnaryOperator<DocxPage<CompositeRun>> pageTransformer) throws Docx4JException, JAXBException {
        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(input);

        return new DocxPageSource(toMemoryPageSource(wordMLPackage, pageTransformer));
    }

    public static PageSource createFrom(InputStream input) throws Docx4JException, JAXBException {
        return createFrom(input, UnaryOperator.identity());
    }

    public static PageSource createFrom(List<? extends DocxPage<? extends CompositeRun>> docxPages) {
        return new SimplePageSource(renderPages(docxPages));
    }

    private static PageSource toMemoryPageSource(WordprocessingMLPackage docx, UnaryOperator<DocxPage<CompositeRun>> pageTransformer) throws JAXBException, XPathBinderAssociationIsPartialException {
        return new SimplePageSource(extractPages(docx, pageTransformer));
    }

    private static List<TextPage> extractPages(WordprocessingMLPackage docx, UnaryOperator<DocxPage<CompositeRun>> pageTransformer) throws JAXBException, XPathBinderAssociationIsPartialException {
        var pageExtractor = new DefaultDocxPageExtractor(docx, pageTransformer);
        var pages = pageExtractor.extractPages();
        return renderPages(pages);
    }

    private static List<TextPage> renderPages(List<? extends DocxPage<? extends CompositeRun>> docxPages) {
        var renderer = new DefaultDocxPageRenderer();
        return renderer.renderPages(docxPages);
    }

    @Override
    public<R> R dischargeTo(PageConsumer<R> consumer) {
        return memoryPageSource.dischargeTo(consumer);
    }
}