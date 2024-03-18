package nitpeek.io.docx.render;

import jakarta.xml.bind.JAXBException;
import nitpeek.core.api.process.RuleSetProvider;
import nitpeek.core.api.report.ReportingException;
import nitpeek.core.api.translate.Translation;
import nitpeek.core.impl.process.SimpleProcessor;
import nitpeek.io.docx.DocxPageSource;
import nitpeek.io.docx.internal.common.RunRenderer;
import nitpeek.io.docx.internal.pagesource.DefaultDocxPageExtractor;
import nitpeek.io.docx.internal.pagesource.render.SimpleArabicNumberRenderer;
import nitpeek.io.docx.internal.pagesource.render.SimpleRunRenderer;
import nitpeek.io.docx.internal.reporter.DefaultRunSplitEnabler;
import nitpeek.io.docx.internal.reporter.DocxAnnotator;
import nitpeek.io.docx.internal.reporter.DocxReporter;
import nitpeek.io.docx.internal.reporter.SplitEnabler;
import nitpeek.io.docx.types.CompositeRun;
import nitpeek.io.docx.types.DocxPage;
import org.docx4j.jaxb.XPathBinderAssociationIsPartialException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

import java.util.function.BiFunction;
import java.util.function.UnaryOperator;

public final class SimpleDocxAnnotator implements DocxAnnotator {

    private final RuleSetProvider ruleSetProvider;
    private final Translation i18n;
    private final UnaryOperator<DocxPage<CompositeRun>> pageTransformer;

    public SimpleDocxAnnotator(RuleSetProvider ruleSetProvider, Translation i18n, UnaryOperator<DocxPage<CompositeRun>> pageTransformer) {
        this.ruleSetProvider = ruleSetProvider;
        this.i18n = i18n;
        this.pageTransformer = pageTransformer;
    }

    @Override
    public void annotateDocument(WordprocessingMLPackage docx, AnnotationRenderer annotationRenderer) throws ReportingException {

        try {
            var splitEnabler = new SplitEnabler(new DefaultRunSplitEnabler());
            BiFunction<Integer, Integer, RunRenderer> runRendererFactory = (pageIndex, pageCount) -> new SimpleRunRenderer(pageIndex, pageCount, new SimpleArabicNumberRenderer());
            var fullPages = new DefaultDocxPageExtractor(docx, pageTransformer).extractPages();
            var processor = new SimpleProcessor(ruleSetProvider);
            processor.startProcessing(DocxPageSource.createFrom(fullPages));

            var fullPagesSplittable = DocxAnnotator.makePagesSplittable(fullPages, splitEnabler, runRendererFactory);
            var reporter = new DocxReporter(fullPagesSplittable, annotationRenderer);
            var features = processor.getFeatures();
            reporter.reportFeatures(features, i18n);

        } catch (JAXBException | XPathBinderAssociationIsPartialException e) {
            throw new ReportingException("Unable to annotate DOCX document with feature annotations", e);
        }
    }
}