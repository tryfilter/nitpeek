package nitpeek.io.docx.util;

import nitpeek.core.api.process.RuleSetProvider;
import nitpeek.core.api.report.ReportingException;
import nitpeek.core.api.translate.Translation;
import nitpeek.core.impl.config.SimpleContext;
import nitpeek.io.SimpleAnnotator;
import nitpeek.io.docx.internal.pagesource.ParagraphPreservingDocxPageExtractor;
import nitpeek.io.docx.render.HighlightAnnotationRenderer;
import nitpeek.io.docx.render.PerSectionDocxAnnotator;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import static nitpeek.io.SimpleAnnotator.outputPath;

public final class EasyDocxAnnotator implements SimpleAnnotator {
    private final Set<RuleSetProvider> ruleSetProviders;
    private final Translation i18n;

    public EasyDocxAnnotator(Set<RuleSetProvider> ruleSetProviders, Translation i18n) {
        this.ruleSetProviders = ruleSetProviders;
        this.i18n = i18n;
    }

    @Override
    public void annotateFeatures(Path inputDocx, Path outputDirectory) throws ReportingException {

        try (var input = Files.newInputStream(inputDocx);
             var output = Files.newOutputStream(outputPath(inputDocx, outputDirectory))) {

            var annotator = new PerSectionDocxAnnotator(ruleSetProviders, i18n, ParagraphPreservingDocxPageExtractor::new, new SimpleContext());
            var docx = WordprocessingMLPackage.load(input);
            annotator.annotateDocument(docx, new HighlightAnnotationRenderer(HighlightAnnotationRenderer.HighlightColor.CYAN));

            docx.save(output);
        } catch (IOException e) {
            throw new ReportingException("Exception while annotating features to DOCX document.", e);
        } catch (Docx4JException e) {
            throw new ReportingException("Unable to open DOCX document for annotating.", e);
        }
    }
}
