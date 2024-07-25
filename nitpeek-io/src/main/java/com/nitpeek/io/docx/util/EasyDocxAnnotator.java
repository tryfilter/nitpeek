package com.nitpeek.io.docx.util;

import com.nitpeek.core.api.report.ReportingException.Problem;
import com.nitpeek.io.SimpleAnnotator;
import com.nitpeek.io.docx.ParagraphPreservingDocxNitpicker;
import com.nitpeek.io.docx.render.HighlightAnnotationRenderer;
import com.nitpeek.core.api.process.RuleSetProvider;
import com.nitpeek.core.api.report.ReportingException;
import com.nitpeek.core.api.translate.Translation;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import static com.nitpeek.io.SimpleAnnotator.outputPath;

public final class EasyDocxAnnotator implements SimpleAnnotator {
    private final Set<RuleSetProvider> ruleSetProviders;
    private final Translation i18n;

    public EasyDocxAnnotator(Set<RuleSetProvider> ruleSetProviders, Translation i18n) {
        this.ruleSetProviders = ruleSetProviders;
        this.i18n = i18n;
    }

    @Override
    public void annotateFeatures(Path inputDocx, Path outputDirectory) throws ReportingException {

        var outputDocx = outputPath(inputDocx, outputDirectory);
        try (var input = Files.newInputStream(inputDocx)) {
            annotate(input, outputDocx);
        } catch (IOException e) {
            throw new ReportingException("Unable to open input file " + inputDocx + " for annotating", e, Problem.INPUT);
        }
    }

    private void annotate(InputStream input, Path outputDocx) throws ReportingException {
        try (var output = Files.newOutputStream(outputDocx)) {
            var nitpicker = new ParagraphPreservingDocxNitpicker(i18n, new HighlightAnnotationRenderer(HighlightAnnotationRenderer.HighlightColor.CYAN));
            nitpicker.nitpick(input, output, ruleSetProviders);
        } catch (IOException e) {
            throw new ReportingException("Unable to open output file " + outputDocx + " for annotating", e, Problem.OUTPUT);
        }
    }
}
