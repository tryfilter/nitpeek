package nitpeek.io.docx.render;

import jakarta.xml.bind.JAXBException;
import nitpeek.core.api.analyze.Rule;
import nitpeek.core.api.process.RuleSetProvider;
import nitpeek.core.api.report.ReportingException;
import nitpeek.core.api.translate.Translation;
import nitpeek.core.impl.process.SimpleProcessor;
import nitpeek.io.docx.DocxPageSource;
import nitpeek.io.docx.internal.common.RunRenderer;
import nitpeek.io.docx.internal.pagesource.DefaultDocxPageExtractor;
import nitpeek.io.docx.internal.pagesource.render.SimpleArabicNumberRenderer;
import nitpeek.io.docx.internal.pagesource.render.SimpleRunRenderer;
import nitpeek.io.docx.internal.reporter.*;
import nitpeek.io.docx.types.SplittableRun;
import org.docx4j.jaxb.XPathBinderAssociationIsPartialException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;

public final class PerSectionDocxAnnotator implements DocxAnnotator {

    private final Translation i18n;
    private final BodyFootnotesRuleSetPartitioner ruleSetPartitioner;

    public PerSectionDocxAnnotator(Set<RuleSetProvider> ruleSetProviders, Translation i18n) {
        this.i18n = i18n;
        this.ruleSetPartitioner = new BodyFootnotesRuleSetPartitioner(ruleSetProviders);
    }

    @Override
    public void annotateDocument(WordprocessingMLPackage docx, AnnotationRenderer annotationRenderer) throws ReportingException {
        try {
            var splitEnabler = new SplitEnabler(new DefaultRunSplitEnabler());
            BiFunction<Integer, Integer, RunRenderer> runRendererFactory = (pageIndex, pageCount) -> new SimpleRunRenderer(pageIndex, pageCount, new SimpleArabicNumberRenderer());
            var originalPages = new DefaultDocxPageExtractor(docx, UnaryOperator.identity()).extractPages();
            var pageAdapter = new BodyAndFootnotesPageAdapter(originalPages, splitEnabler, runRendererFactory);
            var fullPages = pageAdapter.fullPages();
            var bodyOnlyPages = pageAdapter.bodyOnlyPages();
            var footnotesOnlyPages = pageAdapter.footnotesOnlyPages();

            reportFor(fullPages, ruleSetPartitioner.rulesApplicableUniversally(), annotationRenderer);
            reportFor(bodyOnlyPages, ruleSetPartitioner.rulesApplicableToBody(), annotationRenderer);
            reportFor(footnotesOnlyPages, ruleSetPartitioner.rulesApplicableToFootnotes(), annotationRenderer);
        } catch (JAXBException | XPathBinderAssociationIsPartialException e) {
            throw new ReportingException("Unable to annotate DOCX document with feature annotations", e);
        }
    }

    private void reportFor(List<? extends SegmentedDocxPage<SplittableRun>> pages, Set<Rule> rulesToApply, AnnotationRenderer annotationRenderer) {
        var processor = new SimpleProcessor(rulesToApply);
        processor.startProcessing(DocxPageSource.createFrom(pages));
        var features = processor.getFeatures();
        var reporter = new DocxReporter(pages, annotationRenderer);
        reporter.reportFeatures(features, i18n);
    }
}