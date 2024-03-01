package nitpeek.io.docx.internal.reporter;

import nitpeek.core.api.common.Feature;
import nitpeek.core.api.common.FeatureComponent;
import nitpeek.core.api.translate.Translation;
import nitpeek.io.docx.render.DocxTextSelection;
import nitpeek.io.docx.render.Message;

import java.util.Collection;
import java.util.List;

final class DefaultAnnotationExtractor implements AnnotationExtractor {

    private final TextSelectionTransformer textSelectionTransformer;
    private final String author;

    public DefaultAnnotationExtractor(TextSelectionTransformer textSelectionTransformer, String author) {
        this.textSelectionTransformer = textSelectionTransformer;
        this.author = author;
    }

    @Override
    public List<DocxAnnotation> extractAnnotations(List<Feature> features, Translation i18n) {
        return features
                .stream()
                .map(Feature::getComponents)
                .flatMap(Collection::stream)
                .map(featureComponent -> new DocxAnnotation(getMessage(featureComponent, i18n), getTextSelection(featureComponent)))
                .toList();
    }

    private DocxTextSelection getTextSelection(FeatureComponent featureComponent) {
        return textSelectionTransformer.transform(featureComponent.getCoordinates());
    }

    private Message getMessage(FeatureComponent featureComponent, Translation i18n) {
        return new Message(featureComponent.getDescription(i18n), author);
    }
}
