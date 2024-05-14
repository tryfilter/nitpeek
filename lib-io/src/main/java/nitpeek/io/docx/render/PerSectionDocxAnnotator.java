package nitpeek.io.docx.render;

import nitpeek.core.api.analyze.Rule;
import nitpeek.core.api.config.Configuration;
import nitpeek.core.api.config.Context;
import nitpeek.core.api.config.standard.Footnotes;
import nitpeek.core.api.process.RuleSetProvider;
import nitpeek.core.api.report.ReportingException;
import nitpeek.core.api.translate.Translation;
import nitpeek.core.impl.config.ConfiguredPageProcessor;
import nitpeek.core.impl.process.RulesBasedPageConsumer;
import nitpeek.core.impl.process.SimpleProcessor;
import nitpeek.io.docx.DocxPageSource;
import nitpeek.io.docx.internal.common.RunRendererFactory;
import nitpeek.io.docx.internal.pagesource.DefaultFootnoteContentExtractor;
import nitpeek.io.docx.internal.pagesource.DefaultFootnoteReferenceExtractor;
import nitpeek.io.docx.internal.pagesource.DocxFootnotesProvider;
import nitpeek.io.docx.internal.pagesource.render.SimpleArabicNumberRenderer;
import nitpeek.io.docx.internal.pagesource.render.SimpleRunRenderer;
import nitpeek.io.docx.internal.reporter.*;
import nitpeek.io.docx.types.CompositeRun;
import nitpeek.io.docx.types.DocxPage;
import nitpeek.io.docx.types.SplittableRun;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

import java.util.List;
import java.util.Set;
import java.util.function.UnaryOperator;

public final class PerSectionDocxAnnotator implements DocxAnnotator {

    private final Translation i18n;
    private final BodyFootnotesRuleSetPartitioner ruleSetPartitioner;

    private final PageExtractorFactory pageExtractorFactory;

    private final Context context;

    public PerSectionDocxAnnotator(Set<RuleSetProvider> ruleSetProviders, Translation i18n, PageExtractorFactory pageExtractorFactory, Context context) {
        this.i18n = i18n;
        this.ruleSetPartitioner = new BodyFootnotesRuleSetPartitioner(ruleSetProviders);
        this.pageExtractorFactory = pageExtractorFactory;
        this.context = context;
    }

    @Override
    public void annotateDocument(WordprocessingMLPackage docx, AnnotationRenderer annotationRenderer) throws ReportingException {
        var splitEnabler = new SplitEnabler(new DefaultRunSplitEnabler());
        RunRendererFactory runRendererFactory = (pageIndex, pageCount) -> new SimpleRunRenderer(pageIndex, pageCount, new SimpleArabicNumberRenderer());
        var pageExtractor = pageExtractorFactory.createExtractor(docx, UnaryOperator.identity());
        var originalPages = pageExtractor.extractPages();
        var pageAdapter = new BodyAndFootnotesPageAdapter(originalPages, splitEnabler, runRendererFactory);

        var fullPages = pageAdapter.fullPages();
        var bodyOnlyPages = pageAdapter.bodyOnlyPages();
        var footnotesOnlyPages = pageAdapter.footnotesOnlyPages();

        context.registerWithOverwrite(assembleFootnotes(bodyOnlyPages, runRendererFactory), Footnotes.class);
        var currentConfig = context.getConfiguration();

        reportFor(fullPages, ruleSetPartitioner.rulesApplicableUniversally(), annotationRenderer, currentConfig);
        reportFor(bodyOnlyPages, ruleSetPartitioner.rulesApplicableToBody(), annotationRenderer,currentConfig);
        reportFor(footnotesOnlyPages, ruleSetPartitioner.rulesApplicableToFootnotes(), annotationRenderer, currentConfig);
    }

    private Footnotes assembleFootnotes(List<? extends DocxPage<? extends CompositeRun>> bodyOnlyPages, RunRendererFactory runRendererFactory) {
        var footnotesProvider = new DocxFootnotesProvider(bodyOnlyPages, new DefaultFootnoteReferenceExtractor(runRendererFactory, bodyOnlyPages.size()), new DefaultFootnoteContentExtractor(runRendererFactory, bodyOnlyPages.size()));
        return footnotesProvider.getFootnotes();
    }

    private void reportFor(List<? extends SegmentedDocxPage<SplittableRun>> pages, Set<Rule> rulesToApply, AnnotationRenderer annotationRenderer, Configuration config) {
        var processor = new SimpleProcessor(new RulesBasedPageConsumer(rulesToApply, new ConfiguredPageProcessor(config)));
        processor.startProcessing(DocxPageSource.createFrom(pages));
        var features = processor.getFeatures();
        var reporter = new DocxReporter(pages, annotationRenderer);
        reporter.reportFeatures(features, i18n);
    }
}