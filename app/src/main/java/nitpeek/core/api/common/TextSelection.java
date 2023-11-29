package nitpeek.core.api.common;

public record TextSelection(TextCoordinate fromInclusive, TextCoordinate toInclusive) {
    public TextSelection {
        if (fromInclusive.page() > toInclusive.page())
            throw new IllegalArgumentException("from-page may not be > to-page (" + fromInclusive.page() + ">" + toInclusive.page() + ")");
        boolean pagesAreEqual = fromInclusive.page() == toInclusive.page();
        if (pagesAreEqual && fromInclusive.line() > toInclusive.line())
            throw new IllegalArgumentException("from-line may not be > to-line when pages are equal (" + fromInclusive.line() + ">" + toInclusive.line() + ")");
        boolean pagesAndLinesAreEqual = pagesAreEqual && (fromInclusive.line() == toInclusive.line());
        if (pagesAndLinesAreEqual && fromInclusive.character() > toInclusive.character())
            throw new IllegalArgumentException("from-character may not be > to-character when pages and lines are equal (" + fromInclusive.character() + ">" + toInclusive.character() + ")");
    }
}
