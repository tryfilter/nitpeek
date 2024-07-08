package com.nitpeek.io.pdf.util;

import com.nitpeek.core.api.process.RuleSetProvider;
import com.nitpeek.core.api.report.ReportingException;
import com.nitpeek.core.api.translate.Translation;
import com.nitpeek.core.impl.process.RulesBasedPageConsumer;
import com.nitpeek.core.impl.process.SimplePageProcessor;
import com.nitpeek.core.impl.process.SimpleProcessor;
import com.nitpeek.io.SimpleAnnotator;
import com.nitpeek.io.pdf.PdfCommentReporter;
import com.nitpeek.io.pdf.PdfPageSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.nitpeek.io.SimpleAnnotator.outputPath;

public final class EasyPdfAnnotator implements SimpleAnnotator {

    private final RuleSetProvider ruleSetProvider;
    private final Translation i18n;

    public EasyPdfAnnotator(RuleSetProvider ruleSetProvider, Translation i18n) {
        this.ruleSetProvider = ruleSetProvider;
        this.i18n = i18n;
    }

    @Override
    public void annotateFeatures(Path inputPdf, Path outputDirectory) throws ReportingException {

        var processor = new SimpleProcessor(new RulesBasedPageConsumer(ruleSetProvider.getRules(), new SimplePageProcessor()));

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