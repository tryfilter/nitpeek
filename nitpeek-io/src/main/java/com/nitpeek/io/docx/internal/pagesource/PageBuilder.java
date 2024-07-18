package com.nitpeek.io.docx.internal.pagesource;

import com.nitpeek.io.docx.internal.common.SimpleDocxSegment;
import com.nitpeek.io.docx.types.CompositeRun;
import com.nitpeek.io.docx.types.DocxSegment;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.Ftr;
import org.docx4j.wml.Hdr;
import org.docx4j.wml.P;

import java.util.*;

public final class PageBuilder {

    private final WordprocessingMLPackage docx;
    private final ParagraphTransformer paragraphTransformer;
    private final int pageNumber;
    private final Set<Integer> footnoteReferences;

    private final List<P> bodyParagraphs;

    public PageBuilder(WordprocessingMLPackage docx, ParagraphTransformer paragraphTransformer, int pageNumber) {
        this(docx, paragraphTransformer, pageNumber, new ArrayList<>(), new HashSet<>());
    }

    public PageBuilder copy() {
        return new PageBuilder(
                this.docx,
                this.paragraphTransformer,
                this.pageNumber,
                this.bodyParagraphs,
                this.footnoteReferences);
    }

    private PageBuilder(
            WordprocessingMLPackage docx,
            ParagraphTransformer paragraphTransformer,
            int pageNumber,
            List<P> bodyParagraphs,
            Set<Integer> footnoteReferences

    ) {
        this.docx = docx;
        this.paragraphTransformer = paragraphTransformer;
        this.pageNumber = pageNumber;
        this.bodyParagraphs = new ArrayList<>(bodyParagraphs);
        this.footnoteReferences = new HashSet<>(footnoteReferences);
    }

    public PageBuilder addFootnoteReferences(Collection<Integer> footnoteReferences) {
        this.footnoteReferences.addAll(footnoteReferences);
        return this;
    }


    public PageBuilder addBodyParagraph(P paragraph) {
        bodyParagraphs.add(paragraph);
        return this;
    }

    public boolean hasBodyContent() {
        return !bodyParagraphs.isEmpty();
    }

    public SimpleDocxPage<CompositeRun> build() {
        var header = new ComponentSegmentExtractor<>(docx, Hdr.class, pageNumber, paragraphTransformer).extractSegment().orElse(null);
        var footer = new ComponentSegmentExtractor<>(docx, Ftr.class, pageNumber, paragraphTransformer).extractSegment().orElse(null);
        var body = assembleBodySegment(bodyParagraphs, paragraphTransformer);
        var footnotes = computeFootnotes();

        return new SimpleDocxPage<>(header, body, footnotes, footer);
    }

    private DocxSegment<CompositeRun> assembleBodySegment(List<P> paragraphs, ParagraphTransformer paragraphTransformer) {
        return new SimpleDocxSegment<>(paragraphs.stream().map(paragraphTransformer::transform).toList());
    }

    private Map<Integer, DocxSegment<CompositeRun>> computeFootnotes() {
        var result = new HashMap<Integer, DocxSegment<CompositeRun>>();
        for (int footnoteRef : footnoteReferences) {
            var footnote = new FootnotesSegmentExtractor(docx, footnoteRef, paragraphTransformer).extractSegment();
            footnote.ifPresent(f -> result.put(footnoteRef, f));
        }

        return result;
    }
}
