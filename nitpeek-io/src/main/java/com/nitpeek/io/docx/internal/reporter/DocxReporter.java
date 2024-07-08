package com.nitpeek.io.docx.internal.reporter;

import com.nitpeek.core.api.common.Feature;
import com.nitpeek.core.api.report.Reporter;
import com.nitpeek.core.api.translate.Translation;
import com.nitpeek.io.docx.types.DocxSegment;
import com.nitpeek.io.docx.internal.common.Partitioned;
import com.nitpeek.io.docx.internal.common.RunRenderer;
import com.nitpeek.io.docx.internal.pagesource.render.SimpleArabicNumberRenderer;
import com.nitpeek.io.docx.internal.pagesource.render.SimpleRunRenderer;
import com.nitpeek.io.docx.render.AnnotationRenderer;
import com.nitpeek.io.docx.types.SplittableRun;

import java.util.List;

/**
 * This reporter doesn't make any changes permanent.
 * Persisting (e.g. by saving the document the pages belong to to a file on disk) must be done externally after the
 * reporter has finished.
 */
public final class DocxReporter implements Reporter {

    private static final String AUTHOR_NAME = "nitpeek";

    private final List<? extends Partitioned<? extends DocxSegment<? extends SplittableRun>>> pagesToAnnotate;

    private final AnnotationPreparer annotationPreparer = new AnnotationPreparer();

    private final AnnotationRenderer annotationRenderer;

    /**
     * @param pagesToAnnotate    the DOCX pages to report the features to
     * @param annotationRenderer the renderer to use when inserting annotations in the DOCX document
     */
    public DocxReporter(List<? extends Partitioned<? extends DocxSegment<? extends SplittableRun>>> pagesToAnnotate, AnnotationRenderer annotationRenderer) {
        this.pagesToAnnotate = pagesToAnnotate;
        this.annotationRenderer = annotationRenderer;
    }

    @Override
    public void reportFeatures(List<Feature> features, Translation i18n) {
        addAnnotations(features, i18n);
    }

    private void addAnnotations(List<Feature> features, Translation i18n) {
        var annotationExtractor = new DefaultAnnotationExtractor(new PagesTextSelectionTransformer(pagesToAnnotate, this::getRunRenderer), AUTHOR_NAME);
        var annotations = annotationExtractor.extractAnnotations(features, i18n);
        renderAnnotations(annotations);
    }

    private void renderAnnotations(List<DocxAnnotation<SplittableRun>> annotations) {
        var renderableAnnotations = annotations.stream().map(annotationPreparer::prepare).toList();
        renderableAnnotations.forEach(annotationRenderer::renderAnnotation);
    }

    private RunRenderer getRunRenderer(int currentPage, int pageCount) {
        return new SimpleRunRenderer(currentPage, pageCount, new SimpleArabicNumberRenderer());
    }
}