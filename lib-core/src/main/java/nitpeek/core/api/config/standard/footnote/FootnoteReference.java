package nitpeek.core.api.config.standard.footnote;

import nitpeek.core.api.common.TextSelection;

import java.util.Objects;

/**
 * @param ordinal   the (1 based) ordinal of the footnote this reference points to
 * @param selection the {@code TextSelection} that covers the characters representing the reference, in the
 *                  coordinate system defined by only the footnote portions of the pages
 */
public record FootnoteReference(int ordinal, TextSelection selection) {

    // While a document may have the same footnote ordinal more than once (say if it contains multiple distinct chapters),
    // each footnote reference should map to a distinct selection in the text. Hence we only use the reference in equals & hashCode.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FootnoteReference that = (FootnoteReference) o;
        return Objects.equals(selection, that.selection);
    }

    @Override
    public int hashCode() {
        return Objects.hash(selection);
    }
}