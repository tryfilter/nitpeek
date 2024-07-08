package com.nitpeek.io.docx.internal.common;

import com.nitpeek.io.docx.types.CompositeRun;
import com.nitpeek.io.docx.types.DocxSegment;

import java.util.List;

public record SimpleDocxTextSelection<C extends CompositeRun>(
        DocxSegment<C> segment,
        int indexOfFirstCharacter,
        int indexOfLastCharacter) implements DocxTextSelection<C> {

    public static <T extends CompositeRun> DocxTextSelection<T> empty() {
        return new SimpleDocxTextSelection<>(new SimpleDocxSegment<>(List.of()), 0, 0);
    }
}