package com.nitpeek.io.docx.internal.pagesource;

import com.nitpeek.io.docx.internal.common.RunRendererFactory;
import com.nitpeek.io.docx.internal.pagesource.render.ParagraphRenderer;
import com.nitpeek.io.docx.internal.pagesource.render.SimpleParagraphRenderer;
import com.nitpeek.io.docx.types.CompositeRun;
import com.nitpeek.io.docx.types.DocxPage;
import com.nitpeek.io.docx.types.DocxParagraph;
import com.nitpeek.core.api.common.TextCoordinate;
import com.nitpeek.core.api.config.standard.footnote.FootnoteReference;

import java.util.HashSet;
import java.util.Set;

public final class DefaultFootnoteReferenceExtractor implements FootnoteReferenceExtractor {

    private final RunRendererFactory runRendererFactory;
    private final int pageCount;

    public DefaultFootnoteReferenceExtractor(RunRendererFactory runRendererFactory, int pageCount) {
        this.runRendererFactory = runRendererFactory;
        this.pageCount = pageCount;
    }

    @Override
    public Set<FootnoteReference> extractFootnoteReferences(DocxPage<? extends CompositeRun> docxPage, int pageIndex) {
        var result = new HashSet<FootnoteReference>();
        int paragraphIndex = 0;
        for (var paragraph : docxPage.getBody().paragraphs()) {
            result.addAll(extractFootnoteReferences(paragraph, paragraphIndex, pageIndex));
            ++paragraphIndex;
        }

        return Set.copyOf(result);
    }

    private Set<FootnoteReference> extractFootnoteReferences(DocxParagraph<? extends CompositeRun> paragraph, int paragraphIndex, int pageIndex) {
        var result = new HashSet<FootnoteReference>();
        var runs = paragraph.runs();
        if (runs.isEmpty()) return Set.of();

        var renderer = new SimpleParagraphRenderer(runRendererFactory.createRunRenderer(pageIndex, pageCount));

        var firstRun = runs.getFirst();
        firstRun.getFootnoteReference().ifPresent(footnoteOrdinal -> {
            int characterIndex = 0; // first character in paragraph since the footnote reference is the first run
            int footnoteRefLength = length(paragraph.partitionTo(0), renderer);
            result.add(new FootnoteReference(footnoteOrdinal, new TextCoordinate(pageIndex, paragraphIndex, characterIndex).extendToSelection(footnoteRefLength)));
        });

        // Start from index 1: footnote reference already extracted from first run if present (see above)
        for (int runIndex = 1; runIndex < runs.size(); runIndex++) {
            var run = runs.get(runIndex);
            var paragraphBeforeFootnote = paragraph.partitionTo(runIndex - 1);
            var footnote = paragraph.partitionBetween(runIndex, runIndex);
            var footnoteReference = run.getFootnoteReference();

            footnoteReference.ifPresent(footnoteOrdinal -> {
                int characterIndex = length(paragraphBeforeFootnote, renderer);
                int footnoteRefLength = length(footnote, renderer);
                result.add(new FootnoteReference(footnoteOrdinal, new TextCoordinate(pageIndex, paragraphIndex, characterIndex).extendToSelection(footnoteRefLength)));
            });
        }

        return Set.copyOf(result);
    }

    private int length(DocxParagraph<? extends CompositeRun> paragraph, ParagraphRenderer paragraphRenderer) {
        return paragraphRenderer.render(paragraph).length();
    }
}