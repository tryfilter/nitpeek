package nitpeek.io.docx;

import jakarta.xml.bind.JAXBException;
import nitpeek.core.api.common.Feature;
import nitpeek.core.api.report.Reporter;
import nitpeek.core.api.report.ReportingException;
import nitpeek.core.api.translate.Translation;
import nitpeek.io.docx.internal.pagesource.*;
import nitpeek.io.docx.render.RunRenderer;
import nitpeek.io.docx.internal.pagesource.render.SimpleArabicNumberRenderer;
import nitpeek.io.docx.internal.pagesource.render.SimpleRunRenderer;
import nitpeek.io.docx.render.SplittableRun;
import nitpeek.io.docx.internal.reporter.*;
import nitpeek.io.docx.render.AnnotationRenderer;
import nitpeek.io.docx.render.RenderableAnnotation;
import org.docx4j.jaxb.XPathBinderAssociationIsPartialException;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public final class DocxReporter implements Reporter {

    private static final String AUTHOR_NAME = "nitpeek";

    private final InputStream originalDocx;
    private final OutputStream annotatedDocx;

    private final AnnotationPreparer annotationPreparer = new AnnotationPreparer();

    private final AnnotationRenderer annotationRenderer;

    /**
     * @param originalDocx  an input stream with the contents of a DOCX file that has been analyzed. The InputStream is
     *                      not closed by this Reporter.
     * @param annotatedDocx an output stream to a new DOCX file that will contain the contents of {@code originalDocx},
     *                      plus any comments corresponding to the features to be processed with {@code reportFeatures}. <br>
     *                      May not point to the same file as {@code originalDocx}, else the file can get corrupted! <br>
     *                      The OutputStream is not closed by this Reporter.
     */
    public DocxReporter(InputStream originalDocx, OutputStream annotatedDocx, AnnotationRenderer annotationRenderer) {
        this.originalDocx = originalDocx;
        this.annotatedDocx = annotatedDocx;
        this.annotationRenderer = annotationRenderer;
    }

    @Override
    public void reportFeatures(List<Feature> features, Translation i18n) throws ReportingException {
        try {
            var docx = WordprocessingMLPackage.load(originalDocx);
            addAnnotations(docx, features, i18n);
            docx.save(annotatedDocx);

        } catch (Docx4JException | JAXBException e) {
            throw new ReportingException("Unable to annotate DOCX with features.", e);
        }
    }

    private void addAnnotations(WordprocessingMLPackage docx, List<Feature> features, Translation i18n) throws JAXBException, XPathBinderAssociationIsPartialException {
        var pages = new DefaultDocxPageExtractor(docx).extractPages();
        var annotationExtractor = new DefaultAnnotationExtractor(new PagesTextSelectionTransformer(pages, this::getRunRenderer), AUTHOR_NAME);
        var annotations = annotationExtractor.extractAnnotations(features, i18n);
        renderAnnotations(annotations);
    }

    private void renderAnnotations(List<DocxAnnotation<SplittableRun>> annotations) {
        var renderableAnnotations = annotations.stream().map(annotationPreparer::prepare).toList();
        for (var annotation : renderableAnnotations.reversed()) {
            annotationRenderer.renderAnnotation(annotation);
        }
    }

    private RunRenderer getRunRenderer(int currentPage, int pageCount) {
        return new SimpleRunRenderer(currentPage, pageCount, new SimpleArabicNumberRenderer());
    }
}
