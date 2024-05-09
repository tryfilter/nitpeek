package nitpeek.io.docx.render;

import jakarta.xml.bind.JAXBException;
import nitpeek.core.api.process.RuleSetProvider;
import nitpeek.core.api.report.ReportingException;
import nitpeek.core.api.translate.Translation;
import nitpeek.core.impl.process.RulesBasedPageConsumer;
import nitpeek.core.impl.process.SimplePageProcessor;
import nitpeek.core.impl.process.SimpleProcessor;
import nitpeek.io.docx.DocxPageSource;
import nitpeek.io.docx.internal.common.RunRenderer;
import nitpeek.io.docx.internal.pagesource.DefaultDocxPageExtractor;
import nitpeek.io.docx.internal.pagesource.DocxPageExtractor;
import nitpeek.io.docx.internal.pagesource.render.SimpleArabicNumberRenderer;
import nitpeek.io.docx.internal.pagesource.render.SimpleRunRenderer;
import nitpeek.io.docx.internal.reporter.*;
import nitpeek.io.docx.types.CompositeRun;
import nitpeek.io.docx.types.DocxPage;
import org.docx4j.jaxb.XPathBinderAssociationIsPartialException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.BiFunction;
import java.util.function.UnaryOperator;

public final class SimpleDocxAnnotator implements DocxAnnotator {

    private static final Logger log = LoggerFactory.getLogger(SimpleDocxAnnotator.class);
    private final RuleSetProvider ruleSetProvider;
    private final Translation i18n;
    private final UnaryOperator<DocxPage<CompositeRun>> pageTransformer;

    private final PageExtractorFactory pageExtractorFactory;

    public SimpleDocxAnnotator(RuleSetProvider ruleSetProvider, Translation i18n, UnaryOperator<DocxPage<CompositeRun>> pageTransformer, PageExtractorFactory pageExtractorFactory) {
        this.ruleSetProvider = ruleSetProvider;
        this.i18n = i18n;
        this.pageTransformer = pageTransformer;
        this.pageExtractorFactory = pageExtractorFactory;
    }

    @Override
    public void annotateDocument(WordprocessingMLPackage docx, AnnotationRenderer annotationRenderer) throws ReportingException {

        var splitEnabler = new SplitEnabler(new DefaultRunSplitEnabler());
        BiFunction<Integer, Integer, RunRenderer> runRendererFactory = (pageIndex, pageCount) -> new SimpleRunRenderer(pageIndex, pageCount, new SimpleArabicNumberRenderer());
        var pageExtractor = pageExtractorFactory.createExtractor(docx, pageTransformer);
        var fullPages = pageExtractor.extractPages();
        var processor = new SimpleProcessor(new RulesBasedPageConsumer(ruleSetProvider.getRules(), new SimplePageProcessor()));
        processor.startProcessing(DocxPageSource.createFrom(fullPages));

        var fullPagesSplittable = DocxAnnotator.makePagesSplittable(fullPages, splitEnabler, runRendererFactory);
        var reporter = new DocxReporter(fullPagesSplittable, annotationRenderer);
        var features = processor.getFeatures();
        reporter.reportFeatures(features, i18n);

    }
    public static DocxPageExtractor defaultExtractorFactory(WordprocessingMLPackage docx, UnaryOperator<DocxPage<CompositeRun>> pageTransformer) throws ReportingException {
        try {
            return new DefaultDocxPageExtractor(docx, pageTransformer);
        } catch (JAXBException | XPathBinderAssociationIsPartialException e) {
            throw new ReportingException("Unable to extract pages from DOCX", e);
        }
    }
}