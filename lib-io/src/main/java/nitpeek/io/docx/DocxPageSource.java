package nitpeek.io.docx;

import jakarta.xml.bind.JAXBException;
import nitpeek.core.api.common.TextPage;
import nitpeek.core.api.process.PageConsumer;
import nitpeek.core.api.process.PageSource;
import nitpeek.core.impl.process.SimplePageSource;
import nitpeek.io.docx.internal.pagesource.DefaultDocxPageExtractor;
import nitpeek.io.docx.internal.pagesource.DefaultDocxPageRenderer;
import org.docx4j.jaxb.XPathBinderAssociationIsPartialException;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

import java.io.InputStream;
import java.util.List;


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
     * @return a page source containing all pages of the provided DOCX
     */
    public static DocxPageSource createFrom(InputStream input) throws Docx4JException, JAXBException {
        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(input);

        return new DocxPageSource(toMemoryPageSource(wordMLPackage));
    }

    private static PageSource toMemoryPageSource(WordprocessingMLPackage docx) throws JAXBException, XPathBinderAssociationIsPartialException {

        return new SimplePageSource(extractPages(docx));
    }

    private static List<TextPage> extractPages(WordprocessingMLPackage docx) throws JAXBException, XPathBinderAssociationIsPartialException {

        var pageExtractor = new DefaultDocxPageExtractor(docx);
        var pages = pageExtractor.extractPages();
        var renderer = new DefaultDocxPageRenderer();
        return renderer.renderPages(pages);
    }

    @Override
    public void dischargeTo(PageConsumer consumer) {
        memoryPageSource.dischargeTo(consumer);
    }
}