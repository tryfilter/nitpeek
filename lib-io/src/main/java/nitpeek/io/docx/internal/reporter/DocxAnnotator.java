package nitpeek.io.docx.internal.reporter;

import nitpeek.core.api.report.ReportingException;
import nitpeek.io.docx.internal.common.RunRendererFactory;
import nitpeek.io.docx.render.AnnotationRenderer;
import nitpeek.io.docx.types.CompositeRun;
import nitpeek.io.docx.types.DocxPage;
import nitpeek.io.docx.types.SplittableRun;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

import java.util.ArrayList;
import java.util.List;

public interface DocxAnnotator {
    void annotateDocument(WordprocessingMLPackage docx, AnnotationRenderer annotationRenderer) throws ReportingException;

    static List<SegmentedDocxPage<SplittableRun>> makePagesSplittable(
            List<? extends DocxPage<? extends CompositeRun>> originalPages,
            SplitEnabler splitEnabler,
            RunRendererFactory runRendererFactory) {
        List<SegmentedDocxPage<SplittableRun>> result = new ArrayList<>(originalPages.size());
        for (int i = 0; i < originalPages.size(); i++) {
            var runRenderer = runRendererFactory.createRunRenderer(i, originalPages.size());
            result.add(splitEnabler.convertPage(originalPages.get(i), runRenderer));
        }
        return result;
    }
}