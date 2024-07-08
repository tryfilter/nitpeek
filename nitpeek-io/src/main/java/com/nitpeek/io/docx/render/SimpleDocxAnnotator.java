package com.nitpeek.io.docx.render;

import com.nitpeek.io.docx.DocxPageSource;
import com.nitpeek.io.docx.internal.pagesource.DefaultDocxPageExtractor;
import com.nitpeek.io.docx.internal.pagesource.DocxPageExtractor;
import com.nitpeek.io.docx.internal.pagesource.render.SimpleRunRenderer;
import com.nitpeek.io.docx.internal.reporter.*;
import jakarta.xml.bind.JAXBException;
import com.nitpeek.core.api.process.RuleSetProvider;
import com.nitpeek.core.api.report.ReportingException;
import com.nitpeek.core.api.translate.Translation;
import com.nitpeek.core.impl.process.RulesBasedPageConsumer;
import com.nitpeek.core.impl.process.SimplePageProcessor;
import com.nitpeek.core.impl.process.SimpleProcessor;
import com.nitpeek.io.docx.internal.common.RunRendererFactory;
import com.nitpeek.io.docx.internal.pagesource.render.SimpleArabicNumberRenderer;
import com.nitpeek.io.docx.types.CompositeRun;
import com.nitpeek.io.docx.types.DocxPage;
import org.docx4j.jaxb.XPathBinderAssociationIsPartialException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

import java.util.function.UnaryOperator;

public final class SimpleDocxAnnotator implements DocxAnnotator {
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
        RunRendererFactory runRendererFactory = (pageIndex, pageCount) -> new SimpleRunRenderer(pageIndex, pageCount, new SimpleArabicNumberRenderer());
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