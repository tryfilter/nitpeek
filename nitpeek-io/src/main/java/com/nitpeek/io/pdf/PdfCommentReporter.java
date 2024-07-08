package com.nitpeek.io.pdf;

import com.nitpeek.core.api.common.Feature;
import com.nitpeek.core.api.report.Reporter;
import com.nitpeek.core.api.report.ReportingException;
import com.nitpeek.core.api.translate.Translation;
import com.nitpeek.core.impl.process.ListPageConsumer;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public final class PdfCommentReporter implements Reporter {

    private final InputStream originalPdf;
    private final OutputStream annotatedPdf;


    /**
     * @param originalPdf  an input stream with the contents of a PDF file that has been analyzed. The InputStream is
     *                     not closed by this Reporter.
     * @param annotatedPdf an output stream to a new PDF file that will contain the contents of {@code originalPdf},
     *                     plus any comments corresponding to the features to be processed with {@code reportFeatures}. <br>
     *                     May not point to the same file as {@code originalPdf}, else the file can get corrupted! <br>
     *                     The OutputStream is not closed by this Reporter.
     */
    public PdfCommentReporter(InputStream originalPdf, OutputStream annotatedPdf) {
        this.originalPdf = originalPdf;
        this.annotatedPdf = annotatedPdf;
    }

    /**
     * @param features the list of features to report as comments in the PDF
     * @param i18n     the Translation to use when reporting the features
     * @throws ReportingException when the original PDF cannot be read or the new PDF cannot be written, or when
     *                            this method is called more than once on the same object
     */
    @Override
    public void reportFeatures(List<Feature> features, Translation i18n) throws ReportingException {
        try (var randomAccess = new RandomAccessReadBuffer(originalPdf);
             var inputPdf = Loader.loadPDF(randomAccess)
        ) {
            var inputPages = new ListPageConsumer(new PdfPageSource(inputPdf)).getPages();
            var sessionExtractor = new SectionExtractor(features, inputPages);
            addCommentsForFeatures(inputPdf, sessionExtractor, i18n);

            inputPdf.save(annotatedPdf);
        } catch (IOException e) {
            throw new ReportingException("Unable to annotate PDF with features.", e);
        }
    }

    private void addCommentsForFeatures(PDDocument pdf, SectionExtractor sectionExtractor, Translation i18n) throws IOException {

        var sections = sectionExtractor.getSections(pdf);
        var annotationInserter = new AnnotationInserter(pdf);
        annotationInserter.insertAnnotationsFor(sections, i18n);
    }
}
