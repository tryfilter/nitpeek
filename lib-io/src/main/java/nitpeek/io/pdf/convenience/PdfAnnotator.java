package nitpeek.io.pdf.convenience;

import nitpeek.core.api.process.RuleSetProvider;
import nitpeek.core.api.report.ReportingException;
import nitpeek.core.api.translate.Translation;
import nitpeek.core.impl.process.SimpleProcessor;
import nitpeek.io.pdf.PdfCommentReporter;
import nitpeek.io.pdf.PdfPageSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class PdfAnnotator {

    private static final String ANNOTATED_PDF_PREFIX = "nitpicked_";
    private final RuleSetProvider ruleSetProvider;
    private final Translation i18n;

    public PdfAnnotator(RuleSetProvider ruleSetProvider, Translation i18n) {
        this.ruleSetProvider = ruleSetProvider;
        this.i18n = i18n;
    }

    public void annotateFeatures(Path inputPdf, Path outputDirectory) throws IOException, ReportingException {

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
        }
    }

    public static Path outputPath(Path inputPdf, Path outputDirectory) {
        return outputDirectory.resolve(outputDirectory.getFileSystem().getPath(ANNOTATED_PDF_PREFIX + inputPdf.getFileName()));
    }
}
