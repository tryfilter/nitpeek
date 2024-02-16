package nitpeek.io.docx.internal.pagesource;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.ContentAccessor;
import org.docx4j.wml.P;

import java.util.Optional;

final class ComponentSegmentExtractor<T extends ContentAccessor> implements DocxSegmentExtractor {

    private final T component;
    private final int pageIndex;

    ComponentSegmentExtractor(WordprocessingMLPackage docx, Class<T> componentClass, int pageIndex) {
        this.pageIndex = pageIndex;
        this.component = DocxUtil.getRelatedObject(docx.getMainDocumentPart().getRelationshipsPart(), componentClass);
    }

    // Simplified implementation: assumes that the components (header/footer) of all pages are identical. This is not
    // guaranteed to be the case.
    @Override
    public Optional<DocxSegment> extractSegment() {

        if (component == null) return Optional.empty();
        return Optional.of(new DocxSegment(DocxUtil.keepElementsOfType(component.getContent(), P.class)));
    }

}
