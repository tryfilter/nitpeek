package nitpeek.io.docx.internal.render;

import nitpeek.io.docx.internal.JaxbUtil;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.ContentAccessor;

import java.util.List;

public final class  SimpleComponentRenderer<T extends ContentAccessor> implements GenericRenderer {

    private final T component;
    private final ContentRenderer contentRenderer;

    public SimpleComponentRenderer(WordprocessingMLPackage docx, Class<T> componentClass, ContentRenderer contentRenderer) {
        var document = docx.getMainDocumentPart();
        var relationshipsPart = document.getRelationshipsPart();
        this.component = JaxbUtil.getRelatedObject(relationshipsPart, componentClass);
        this.contentRenderer = contentRenderer;
    }

    @Override
    public List<String> renderParagraphs() {
        return contentRenderer.renderParagraphs(component.getContent());
    }
}
