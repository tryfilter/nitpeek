package com.nitpeek.io.docx.types;

import com.nitpeek.io.docx.internal.common.Partitioned;

import java.util.Collection;
import java.util.List;

public interface DocxSegment<C extends CompositeRun> extends Partitioned<DocxSegment<C>> {
    List<DocxParagraph<C>> paragraphs();
    default List<C> componentRuns() {
        return paragraphs()
                .stream()
                .map(DocxParagraph::runs)
                .flatMap(Collection::stream)
                .toList();
    }
}