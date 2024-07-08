package com.nitpeek.io.docx.types;

import com.nitpeek.io.docx.internal.common.DocxUtil;
import org.docx4j.wml.CTFtnEdnRef;
import org.docx4j.wml.R;

import java.util.List;
import java.util.Optional;

public interface CompositeRun {

    List<R> componentRuns();

    default Optional<Integer> getFootnoteReference() {
        var runs = componentRuns();
        if (runs.isEmpty()) return Optional.empty();

        var run = runs.getFirst();
        var footnoteReferences = DocxUtil.getElementValues(run, CTFtnEdnRef.class);
        if (footnoteReferences.isEmpty()) return Optional.empty();
        var footnoteReference = footnoteReferences.getFirst();
        return Optional.of(footnoteReference.getId().intValue());
    }
}