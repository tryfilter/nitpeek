package nitpeek.io.pdf.internal;

import nitpeek.core.api.common.FeatureComponent;
import nitpeek.core.api.translate.Translation;
import nitpeek.core.impl.common.SimpleFeatureComponent;
import nitpeek.core.impl.translate.IdentityTranslation;
import nitpeek.io.pdf.internal.SectionExtractor.ComponentWithSections;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.IOException;
import java.util.List;

public final class AnnotationInserter {

    private final Translation i18nNoop = new IdentityTranslation();
    private final PDDocument pdf;
    private final AnnotationCreator annotationCreator = new AnnotationCreator();

    public AnnotationInserter(PDDocument pdf) {
        this.pdf = pdf;
    }

    public void insertAnnotationsFor(List<ComponentWithSections> sections, Translation i18n) throws IOException {
        for (var section : sections) {
            saveAnnotations(section, i18n);
        }
    }

    private void saveAnnotations(ComponentWithSections componentWithSections, Translation i18n) throws IOException {
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
