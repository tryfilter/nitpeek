package com.nitpeek.io.docx.internal.common;

import com.nitpeek.io.docx.types.CompositeRun;
import com.nitpeek.io.docx.types.DocxParagraph;
import com.nitpeek.io.docx.types.DocxSegment;

import java.util.List;

/**
 * Represents a segment of contiguous text, with the extents being defined at the "run" granularity level.
 */
public final class SimpleDocxSegment<C extends CompositeRun> implements DocxSegment<C> {
    private final List<DocxParagraph<C>> paragraphs;

    public SimpleDocxSegment(List<? extends DocxParagraph<C>> paragraphs) {
        this.paragraphs = List.copyOf(paragraphs);
    }


    @Override
    public DocxSegment<C> fullPartition() {
        return this;
    }

    @Override
    public DocxSegment<C> partitionBetween(int firstIndex, int lastIndex) {
        return new SimpleDocxSegment<>(paragraphs.subList(firstIndex, lastIndex + 1));
    }

    @Override
    public DocxSegment<C> partitionFrom(int firstIndex) {
        return partitionBetween(firstIndex, paragraphs.size() - 1);
    }

    @Override
    public DocxSegment<C> partitionTo(int lastIndex) {
        return partitionBetween(0, lastIndex);
    }

    @Override
    public List<DocxParagraph<C>> paragraphs() {
        return paragraphs;
    }
}