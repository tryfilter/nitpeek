package com.nitpeek.io.pdf;

import com.nitpeek.core.api.common.FeatureComponent;
import com.nitpeek.core.api.translate.Translation;
import com.nitpeek.core.impl.common.SimpleFeatureComponent;
import com.nitpeek.core.impl.translate.IdentityTranslation;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.IOException;
import java.util.List;

final class AnnotationInserter {

    private final Translation i18nNoop = new IdentityTranslation();
    private final PDDocument pdf;
    private final AnnotationCreator annotationCreator = new AnnotationCreator();

    public AnnotationInserter(PDDocument pdf) {
        this.pdf = pdf;
    }

    public void insertAnnotationsFor(List<SectionExtractor.ComponentWithSections> sections, Translation i18n) throws IOException {
        for (var section : sections) {
            saveAnnotations(section, i18n);
        }
    }

    private void saveAnnotations(SectionExtractor.ComponentWithSections componentWithSections, Translation i18n) throws IOException {
        var featureComponent = componentWithSections.component();
        var sections = componentWithSections.sections();
        if (componentWithSections.sections().isEmpty()) return;
        var firstSection = componentWithSections.sections().getFirst();
        save(firstSection, featureComponent, i18n);

        saveAllExceptFirstWithoutDescription(sections, featureComponent);

    }

    private void saveAllExceptFirstWithoutDescription(List<SectionExtractor.Section> sections, FeatureComponent originalComponent) throws IOException {
        var componentWithNoDescription = new SimpleFeatureComponent(
                "",
                originalComponent.getCoordinates(),
                originalComponent.getRelevantTextPortion().orElse("")
        );

        for (SectionExtractor.Section section : sections.stream().skip(1).toList()) {
            save(section, componentWithNoDescription, i18nNoop);
        }
    }

    private void save(SectionExtractor.Section section, FeatureComponent featureComponent, Translation i18n) throws IOException {

        var annotation = annotationCreator.create(section, featureComponent, i18n);
        int relevantPage = section.page();
        var relevantAnnotations = pdf.getPage(relevantPage).getAnnotations();

        relevantAnnotations.add(annotation);
    }
}
