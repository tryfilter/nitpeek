package nitpeek.io.docx.internal.pagesource;

import nitpeek.io.docx.internal.common.*;
import nitpeek.io.docx.render.CompositeRun;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.ContentAccessor;
import org.docx4j.wml.P;

import java.util.List;
import java.util.Optional;

final class ComponentSegmentExtractor<T extends ContentAccessor> implements DocxSegmentExtractor {

    private final T component;
    private final int pageIndex;
    private final ParagraphTransformer paragraphTransformer;

    ComponentSegmentExtractor(WordprocessingMLPackage docx, Class<T> componentClass, int pageIndex, ParagraphTransformer paragraphTransformer) {
        this.pageIndex = pageIndex;
        this.paragraphTransformer = paragraphTransformer;
        this.component = DocxUtil.getRelatedObject(docx.getMainDocumentPart().getRelationshipsPart(), componentClass);
    }

    // Simplified implementation: assumes that the components (header/footer) of all pages are identical. This is not
    // guaranteed to be the case.
    @Override
    public Optional<DocxSegment<CompositeRun>> extractSegment() {

        if (component == null) return Optional.empty();
        List<P> originalParagraphs = DocxUtil.getNonEmptyParagraphs(component.getContent());
        List<DocxParagraph<CompositeRun>> transformedParagraphs = originalParagraphs.stream().map(paragraphTransformer::transform).toList();
        return Optional.of(new SimpleDocxSegment<>(transformedParagraphs));
    }

}
