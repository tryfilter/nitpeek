package nitpeek.io.pdf.internal;

import nitpeek.core.api.common.FeatureComponent;
import nitpeek.core.api.translate.Translation;
import nitpeek.io.pdf.internal.SectionExtractor.Section;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationHighlight;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationTextMarkup;

final class AnnotationCreator {

    private final String author;

    public AnnotationCreator() {
        this("nitpeek");
    }

    public AnnotationCreator(String author) {
        this.author = author;
    }

    public PDAnnotation create(Section section, FeatureComponent featureComponent, Translation i18n) {
        PDAnnotationTextMarkup annotation = new PDAnnotationHighlight();

        annotation.setRectangle(new PDRectangle());
        annotation.setQuadPoints(getQuadPoints(section));

        annotation.setColor(getColor());
        annotation.setTitlePopup(author);

        annotation.setContents(featureComponent.getDescription(i18n));
        return annotation;
    }

    private float[] getQuadPoints(Section section) {
        float verticalPadding = 3.5f;
        float horizontalPadding = -2.2f;
        var first = section.start().position();
        var last = section.end().position();
        float minX = first.getXDirAdj() - horizontalPadding;
        float minY = first.getPageHeight() - first.getYDirAdj() - verticalPadding;
        float maxX = last.getXDirAdj() + last.getWidthDirAdj() + horizontalPadding;
        float maxY = last.getPageHeight() - last.getYDirAdj() + last.getHeightDir() + verticalPadding;

        return new float[]{
                minX, maxY,
                maxX, maxY,
                minX, minY,
                maxX, minY
        };
    }

    private PDColor getColor() {
        return new PDColor(new float[]{0, 0.8f, 0.88f}, PDDeviceRGB.INSTANCE);
    }
}
