package com.nitpeek.io.docx.testutil;

import com.nitpeek.io.docx.internal.common.PartialParagraph;
import com.nitpeek.io.docx.internal.common.SimpleDocxSegment;
import com.nitpeek.io.docx.internal.common.SingletonRun;
import com.nitpeek.io.docx.internal.pagesource.ComplexRun;
import com.nitpeek.io.docx.internal.pagesource.SimpleDocxPage;
import com.nitpeek.io.docx.types.CompositeRun;
import com.nitpeek.io.docx.types.DocxPage;
import com.nitpeek.io.docx.types.DocxParagraph;
import com.nitpeek.io.docx.types.DocxSegment;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.docx4j.wml.*;

import java.util.*;

public final class DocxTestUtil {

    private static final ObjectFactory objectFactory = new ObjectFactory();

    private DocxTestUtil() {
    }

    public static R createSampleRun(String text) {
        R run = objectFactory.createR();
        addText(run, text);
        return run;
    }

    public static CompositeRun run(String text) {
        return new SingletonRun(createSampleRun(text));
    }

    public static CompositeRun complexRun(String... text) {
        return new ComplexRun(Arrays.stream(text).map(DocxTestUtil::createSampleRun).toList());
    }

    private static void addText(R run, String text) {
        var textElement = objectFactory.createText();
        textElement.setValue(text);
        run.getContent().add(objectFactory.createRT(textElement));
    }

    public static void applyStyleItalicBlueHighlight(R run) {
        RPr runProperties = new RPr();
        runProperties.setI(new BooleanDefaultTrue());
        var highlight = objectFactory.createHighlight();
        highlight.setVal("blue");
        runProperties.setHighlight(highlight);
        run.setRPr(runProperties);
    }

    public static void applyStyleItalicBoldTimesNewRomanYellow(R run) {
        RPr runProperties = new RPr();
        runProperties.setI(new BooleanDefaultTrue());
        runProperties.setB(new BooleanDefaultTrue());
        var font = objectFactory.createRFonts();
        font.setAscii("Times New Roman");
        font.setCs("Times New Roman");
        font.setHAnsi("Times New Roman");
        runProperties.setRFonts(font);
        var color = objectFactory.createColor();
        color.setVal("FFFF00");
        runProperties.setColor(color);
        run.setRPr(runProperties);
    }

    public static void applyStyleIntenseEmphasisRedHighlightUnderline(R run) {
        RPr runProperties = new RPr();
        var style = objectFactory.createRStyle();
        style.setVal("IntenseEmphasis");
        runProperties.setRStyle(style);
        var highlight = objectFactory.createHighlight();
        highlight.setVal("red");
        runProperties.setHighlight(highlight);
        var underline = objectFactory.createU();
        underline.setVal(UnderlineEnumeration.SINGLE);
        runProperties.setU(underline);
        run.setRPr(runProperties);
    }

    @SafeVarargs
    public static <C extends CompositeRun> DocxParagraph<C> paragraph(C... runs) {
        return new PartialParagraph<>(Arrays.asList(runs));
    }

    public static <C extends CompositeRun> DocxSegment<C> segment(List<? extends DocxParagraph<C>> paragraphs) {
        return new SimpleDocxSegment<>(paragraphs);
    }

    public static <C extends CompositeRun> DocxSegment<C> segment(DocxParagraph<C> paragraph) {
        return new SimpleDocxSegment<>(List.of(paragraph));
    }

    public static DocxPage<CompositeRun> createPage(@Nullable CompositeRun headerRun, CompositeRun bodyRun, @Nullable CompositeRun footnoteRun, @Nullable CompositeRun footerRun) {
        var header = headerRun == null ? null : segment(paragraph(headerRun));
        var body = bodyRun == null ? new SimpleDocxSegment<>(List.of()) : segment(paragraph(bodyRun));
        var footnotes = footnoteRun == null ? new HashMap<Integer, DocxSegment<CompositeRun>>() : Map.of(1, segment(paragraph(footnoteRun)));
        var footer = footerRun == null ? null : segment(paragraph(footerRun));

        return new SimpleDocxPage<>(header, body, footnotes, footer);
    }
}
