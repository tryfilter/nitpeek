package com.nitpeek.io.docx.render;

import com.nitpeek.io.docx.internal.common.DocxUtil;
import com.nitpeek.io.docx.internal.common.SingletonRun;
import com.nitpeek.io.docx.render.HighlightAnnotationRenderer.HighlightColor;
import com.nitpeek.io.docx.testutil.DocxTestUtil;
import com.nitpeek.io.docx.types.Message;
import org.docx4j.XmlUtils;
import org.docx4j.wml.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

final class HighlightAnnotationRendererShould {

    HighlightColor highlightColor = HighlightColor.BLUE;
    private final AnnotationRenderer highlightAnnotationRenderer = new HighlightAnnotationRenderer(highlightColor);

    private R runToHighlight;

    @BeforeEach
    void setup() {
        runToHighlight = DocxTestUtil.createSampleRun("Sample text");
    }

    @Test
    void preserveExistingFormatting() {

        DocxTestUtil.applyStyleItalicBoldTimesNewRomanYellow(runToHighlight);
        BooleanDefaultTrue oldBoldValue = runToHighlight.getRPr().getB();
        BooleanDefaultTrue oldItalicsValue = runToHighlight.getRPr().getI();
        highlightAnnotationRenderer.renderAnnotation(new RenderableAnnotation(getMessage(), List.of(new SingletonRun(runToHighlight))));
        assertEquals(oldBoldValue, runToHighlight.getRPr().getB());
        assertEquals(oldItalicsValue, runToHighlight.getRPr().getI());
    }

    @Test
    void preserveExistingFormattingInChangeTracking() {

        DocxTestUtil.applyStyleItalicBoldTimesNewRomanYellow(runToHighlight);
        highlightAnnotationRenderer.renderAnnotation(new RenderableAnnotation(getMessage(), List.of(new SingletonRun(runToHighlight))));
        var changeTrackingRpr = runToHighlight.getRPr().getRPrChange().getRPr();
        assertTrue(containsTaggedJaxbElement("b", changeTrackingRpr)); // bold
        assertTrue(containsTaggedJaxbElement("i", changeTrackingRpr)); // italic
    }

    @Test
    void preserveExistingFont() {
        preserveExistingFormatElement(DocxTestUtil::applyStyleItalicBoldTimesNewRomanYellow, RPrAbstract::getRFonts);
    }

    @Test
    void preserveExistingFontInChangeTracking() {
        preserveExistingFormatElementInChangeTracking(DocxTestUtil::applyStyleItalicBoldTimesNewRomanYellow, RPrAbstract::getRFonts, RFonts.class);
    }

    @Test
    void preserveExistingColor() {
        preserveExistingFormatElement(DocxTestUtil::applyStyleItalicBoldTimesNewRomanYellow, RPrAbstract::getColor);
    }

    @Test
    void preserveExistingColorInChangeTracking() {
        preserveExistingFormatElementInChangeTracking(DocxTestUtil::applyStyleItalicBoldTimesNewRomanYellow, RPrAbstract::getColor, Color.class);
    }

    @Test
    void preserveExistingUnderline() {
        preserveExistingFormatElement(DocxTestUtil::applyStyleItalicBoldTimesNewRomanYellow, RPrAbstract::getU);
    }

    @Test
    void preserveExistingUnderlineInChangeTracking() {
        preserveExistingFormatElementInChangeTracking(DocxTestUtil::applyStyleIntenseEmphasisRedHighlightUnderline, RPrAbstract::getU, U.class);
    }

    @Test
    void preserveExistingStyle() {
        preserveExistingFormatElement(DocxTestUtil::applyStyleIntenseEmphasisRedHighlightUnderline, RPrAbstract::getRStyle);
    }

    @Test
    void preserveExistingStyleInChangeTracking() {
        preserveExistingFormatElementInChangeTracking(DocxTestUtil::applyStyleIntenseEmphasisRedHighlightUnderline, RPrAbstract::getRStyle, RStyle.class);
    }

    @Test
    void addHighlight() {

        DocxTestUtil.applyStyleItalicBoldTimesNewRomanYellow(runToHighlight);
        highlightAnnotationRenderer.renderAnnotation(new RenderableAnnotation(getMessage(), List.of(new SingletonRun(runToHighlight))));

        Highlight highlight = runToHighlight.getRPr().getHighlight();
        assertNotNull(highlight);
        assertEquals(highlightColor.getName(), highlight.getVal());
    }

    @Test
    void addHighlightOfNewColorIfSameHighlightAlreadyPresent() {

        DocxTestUtil.applyStyleItalicBlueHighlight(runToHighlight);
        highlightAnnotationRenderer.renderAnnotation(new RenderableAnnotation(getMessage(), List.of(new SingletonRun(runToHighlight))));

        Highlight highlight = runToHighlight.getRPr().getHighlight();
        assertNotNull(highlight);
        assertNotEquals(highlightColor.getName(), highlight.getVal());
    }

    @Test
    void preserveExistingHighlightInChangeTrackingIfSameColorAsDefaultHighlightColor() {
        preserveExistingFormatElementInChangeTracking(DocxTestUtil::applyStyleItalicBlueHighlight, RPrAbstract::getHighlight, Highlight.class);
    }

    @Test
    void preserveExistingHighlightInChangeTracking() {
        preserveExistingFormatElementInChangeTracking(DocxTestUtil::applyStyleIntenseEmphasisRedHighlightUnderline, RPrAbstract::getHighlight, Highlight.class);
    }

    private void preserveExistingFormatElement(Consumer<R> styler, Function<RPr, ?> elementSelector) {
        styler.accept(runToHighlight);
        var oldElement = elementSelector.apply(runToHighlight.getRPr());
        highlightAnnotationRenderer.renderAnnotation(new RenderableAnnotation(getMessage(), List.of(new SingletonRun(runToHighlight))));
        var newElement = elementSelector.apply(runToHighlight.getRPr());
        assertEquals(oldElement, newElement);
    }

    private void preserveExistingFormatElementInChangeTracking(Consumer<R> styler, Function<RPr, ?> elementSelector, Class<?> elementType) {
        styler.accept(runToHighlight);
        var oldElement = elementSelector.apply(runToHighlight.getRPr());
        highlightAnnotationRenderer.renderAnnotation(new RenderableAnnotation(getMessage(), List.of(new SingletonRun(runToHighlight))));
        var changeTrackingRpr = runToHighlight.getRPr().getRPrChange().getRPr();
        var newElement = DocxUtil.keepElementsOfType(changeTrackingRpr.getEGRPrBase(), elementType).getFirst();

        // marshal to xml because equals() of these elements uses object identity, which of course doesn't hold because
        // we create a new object for the change-tracking rpr
        assertEquals(XmlUtils.marshaltoString(oldElement), XmlUtils.marshaltoString(newElement));
    }

    private boolean containsTaggedJaxbElement(String tagName, CTRPrChange.RPr historicRpr) {
        var jaxbElements = DocxUtil.keepJaxbElements(historicRpr.getEGRPrBase());
        for (var element : jaxbElements) {
            if (tagName.equals(element.getName().getLocalPart())) return true;
        }
        return false;
    }

    private Message getMessage() {
        return new Message("Message Text", "Message author");
    }
}