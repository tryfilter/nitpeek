package com.nitpeek.core.api.config.standard.footnote;

import java.util.Objects;

public record Footnote(FootnoteReference reference, FootnoteContent content) {

    // Per document, only one single footnote corresponds to a specific footnote reference in the text
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Footnote footnote = (Footnote) o;
        return Objects.equals(reference, footnote.reference);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reference);
    }
}