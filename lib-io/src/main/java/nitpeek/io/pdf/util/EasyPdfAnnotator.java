package nitpeek.io.pdf.util;

import nitpeek.core.api.process.RuleSetProvider;
import nitpeek.core.api.report.ReportingException;
import nitpeek.core.api.translate.Translation;
import nitpeek.core.impl.process.SimpleProcessor;
import nitpeek.io.SimpleAnnotator;
import nitpeek.io.pdf.PdfCommentReporter;
import nitpeek.io.pdf.PdfPageSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static nitpeek.io.SimpleAnnotator.outputPath;

public final class EasyPdfAnnotator implements SimpleAnnotator {

    private final RuleSetProvider ruleSetProvider;
    private final Translation i18n;

    public EasyPdfAnnotator(RuleSetProvider ruleSetProvider, Translation i18n) {
        this.ruleSetProvider = ruleSetProvider;
        this.i18n = i18n;
    }

    @Override
    public void annotateFeatures(Path inputPdf, Path outputDirectory) throws ReportingException {

        var processor = new SimpleProcessor(ruleSetProvider);

        try (var input = Files.newInputStream(inputPdf);
             var output = Files.newOutputStream(outputPath(inputPdf, outputDirectory))) {

            var inMemory = new ByteArrayOutputStream();
            input.transferTo(inMemory);
            var pdfToAnalyze = new ByteArrayInputStream(inMemory.toByteArray());
            var pdfToAnnotate = new ByteArrayInputStream(inMemory.toByteArray());

            var pdfSource = PdfPageSource.createFrom(pdfToAnalyze);
            processor.startProcessing(pdfSource);
            var features = processor.getFeatures();

            var reporter = new PdfCommentReporter(pdfToAnnotate, output);
            reporter.reportFeatures(features, i18n);
        } catch (IOException e) {
            throw new ReportingException("Exception while annotating features", e);
        }
    }
}
