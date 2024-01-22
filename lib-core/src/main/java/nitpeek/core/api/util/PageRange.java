package nitpeek.core.api.util;

import nitpeek.core.api.common.TextSelection;

/**
 * Represents a range of pages (inclusive on both ends).
 *
 * @param firstPage
 * @param lastPage
 */
public record PageRange(int firstPage, int lastPage) {
    /**
     * @param coordinate precise text selection to be turned into a more coarse-grained page interval
     * @return the PageRange equivalent of the provided TextSelection. Line and character information is discarded.
     */
    public static PageRange pageRange(TextSelection coordinate) {
        return new PageRange(coordinate.fromInclusive().page(), coordinate.toInclusive().page());
    }
}
