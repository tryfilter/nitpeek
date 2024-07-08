package com.nitpeek.io.docx.internal.reporter;

import com.nitpeek.core.api.common.Feature;
import com.nitpeek.core.api.common.FeatureComponent;
import com.nitpeek.core.api.translate.Translation;
import com.nitpeek.io.docx.types.SplittableRun;
import com.nitpeek.io.docx.internal.common.DocxTextSelection;
import com.nitpeek.io.docx.types.Message;

import java.util.Collection;
import java.util.List;

public final class DefaultAnnotationExtractor implements AnnotationExtractor<SplittableRun> {

    private final TextSelectionTransformer<SplittableRun> textSelectionTransformer;
    private final String author;

    public DefaultAnnotationExtractor(TextSelectionTransformer<SplittableRun> textSelectionTransformer, String author) {
        this.textSelectionTransformer = textSelectionTransformer;
        this.author = author;
    }

    @Override
    public List<DocxAnnotation<SplittableRun>> extractAnnotations(List<Feature> features, Translation i18n) {
        return features
                .stream()
                .map(Feature::getComponents)
                .flatMap(Collection::stream)
                .map(featureComponent -> new DocxAnnotation<>(getMessage(featureComponent, i18n), getTextSelection(featureComponent)))
                .toList();
    }

    private DocxTextSelection<SplittableRun> getTextSelection(FeatureComponent featureComponent) {
        return textSelectionTransformer.transform(featureComponent.getCoordinates());
    }

    private Message getMessage(FeatureComponent featureComponent, Translation i18n) {
        return new Message(featureComponent.getDescription(i18n), author);
    }
}
