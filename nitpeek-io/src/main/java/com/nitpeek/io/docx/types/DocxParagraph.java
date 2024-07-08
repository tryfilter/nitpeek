package com.nitpeek.io.docx.types;

import com.nitpeek.io.docx.internal.common.Partitioned;

import java.util.List;

public interface DocxParagraph<C extends CompositeRun> extends Partitioned<DocxParagraph<C>> {
    List<C> runs();
}
