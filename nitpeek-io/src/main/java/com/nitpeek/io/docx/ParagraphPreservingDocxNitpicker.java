package com.nitpeek.io.docx;

import com.nitpeek.core.api.process.Nitpicker;
import com.nitpeek.core.api.process.RuleSetProvider;
import com.nitpeek.core.api.report.ReportingException;
import com.nitpeek.core.api.translate.Translation;
import com.nitpeek.core.impl.config.SimpleContext;
import com.nitpeek.io.docx.internal.pagesource.ParagraphPreservingDocxPageExtractor;
import com.nitpeek.io.docx.render.AnnotationRenderer;
import com.nitpeek.io.docx.internal.reporter.PerSectionDocxAnnotator;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

/**
 * Treats paragraphs as atomic units that belong to the page on which the paragraph begins.<br>
 * Selectively applies rules to different sections of the input document depending on the presence of specific tags of
 * the namespace com.nitpeek.CATEGORY
 */
public class ParagraphPreservingDocxNitpicker implements Nitpicker {
    private final Translation i18n;
    private final AnnotationRenderer annotationRenderer;

    public ParagraphPreservingDocxNitpicker(Translation i18n, AnnotationRenderer annotationRenderer) {
        this.i18n = i18n;
        this.annotationRenderer = annotationRenderer;
    }

    /**
     * Applies the provided {@code RuleSet}s to an input DOCX document read from the provided {@code InputStream} and
     * writes the resulting annotated DOCX document to the provided {@code OutputStream}
     *
     * @param input           the stream providing the input document; must represent a valid DOCX file. The stream is not closed by this method.
     * @param analysisResult  the stream to which the found features will be reported; will receive the contents of a new
     *                        DOCX document with the same contents as those in the document provided by {@code input}, with any
     *                        features annotated as highlighted text in the resulting document. The stream is not closed by this method
     * @param ruleSetsToApply the collection of {@code RuleSet}s to use for finding features in the input; different {@code RuleSet}s
     *                        will be applied to different sections of the DOCX document depending on which tags of the
     *                        com.nitpeek.CATEGORY namespace are defined on each {@code RuleSet}
     * @throws ReportingException when there is a problem with loading reading the original DOCX document or with writing
     *                            the annotate DOCX document
     */
    @Override
    public void nitpick(InputStream input, OutputStream analysisResult, Set<RuleSetProvider> ruleSetsToApply) throws ReportingException {
        try {
            var annotator = new PerSectionDocxAnnotator(ruleSetsToApply, i18n, ParagraphPreservingDocxPageExtractor::new, new SimpleContext());
            var docx = WordprocessingMLPackage.load(input);
            annotator.annotateDocument(docx, annotationRenderer);
            docx.save(analysisResult);
        } catch (Docx4JException e) {
            throw new ReportingException("Unable to open/save DOCX document", e);
        }
    }
}