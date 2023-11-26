package nitpeek.core.api.common.util;

import nitpeek.core.api.common.TextSelection;

public record PageRange(int firstPage, int lastPage) {
    public static PageRange pageRange(TextSelection coordinate) {
        return new PageRange(coordinate.fromInclusive().page(), coordinate.toInclusive().page());
    }
}
