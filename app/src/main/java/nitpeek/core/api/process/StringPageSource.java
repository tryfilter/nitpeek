package nitpeek.core.api.process;

import nitpeek.core.api.analyze.SimpleTextPage;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import static java.util.stream.IntStream.concat;

public final class StringPageSource implements PageSource {

    private final List<String> pages;

    /**
     * Creates a page source with one single page containing the entire text passed in {@code page}
     *
     * @param page the single page to produce
     */
    public StringPageSource(String page) {
        Objects.requireNonNull(page);
        this.pages = List.of(page);
    }

    /**
     * Creates a page source with one single page for each string passed in {@code pages}
     *
     * @param pages the list of pages to produce from this page source
     */
    public StringPageSource(List<String> pages) {
        Objects.requireNonNull(pages);
        this.pages = new ArrayList<>(pages);
    }

    /**
     * Creates a page source with the text passed in {@code pages} split into separate pages by {@code splitPagesBy}.<br>
     * If {@code splitPagesBy} produces no matches in {@code pages}, a single page with the entire content of {@code pages}
     * will be produced by this page source.
     *
     * @param pages        the string to be split into pages
     * @param splitPagesBy the regex pattern to split pages by; matches of the pattern will be swallowed
     */
    public StringPageSource(String pages, Pattern splitPagesBy) {
        Objects.requireNonNull(pages);
        Objects.requireNonNull(splitPagesBy);
        this.pages = List.of(splitPagesBy.split(pages));
    }

    /**
     * Creates a page source with the text passed in {@code pages} split into separate pages each {@code rowsPerPage} rows long
     *
     * @param pages       the string to be split into pages
     * @param rowsPerPage the number of rows in each page, possibly except the last which may be shorter; must be 1 or greater
     * @throws IllegalArgumentException when {@code rowsPerPage} is smaller than 1
     */
    public StringPageSource(String pages, int rowsPerPage) {
        Objects.requireNonNull(pages);
        if (rowsPerPage <= 0)
            throw new IllegalArgumentException("rowsPerPage must be at least 1 but was " + rowsPerPage);

        this.pages = splitPages(pages, rowsPerPage);

    }

    private List<String> splitPages(String pages, int rowsPerPage) {
        List<String> lines = pages.lines().toList();
        if (rowsPerPage > lines.size()) return List.of(pages);

        final int fullPageCount = lines.size() / rowsPerPage;

        return splitPagesUsing(pages, Collections.nCopies(fullPageCount, rowsPerPage).stream().toList());
    }

    private String joinLines(List<String> lines) {
        return String.join("\n", lines);
    }

    /**
     * Creates a page source with the text passed in {@code pages} split into separate pages, the number of rows in
     * each page being dictated by the corresponding number in {@code pageLengthsInRows}.<br>
     * <br>
     * Note that, if the number of remaining rows in {@code pages} is not sufficient to satisfy the row number
     * specified by the next number in {@code pageLengthsInRows}, the last page will contain fewer lines than specified,
     * and all remaining lengths in {@code pageLengthsInRows} are disregarded.<br>
     * Conversely, if, after generating {@code pageLengthsInRows.size()} pages, there is still text left in {@code pages},
     * all remaining rows will be placed into a final page, leading to a total of {@code pageLengthsInRows.size() + 1}
     * generated pages.
     *
     * @param pages             the string to be split into pages
     * @param pageLengthsInRows the length of each of the pages to be produced by this page source; must be non-empty;
     *                          each element must be strictly positive
     * @throws IllegalArgumentException when {@code pageLengthsInRows} is empty or any of its elements is smaller than 1
     */
    public StringPageSource(String pages, List<Integer> pageLengthsInRows) {
        Objects.requireNonNull(pages);
        Objects.requireNonNull(pageLengthsInRows);
        if (pageLengthsInRows.isEmpty()) throw new IllegalArgumentException("pageLengthsInRows must be non-empty");

        this.pages = splitPagesUsing(pages, pageLengthsInRows);
    }

    private List<String> splitPagesUsing(String pages, List<Integer> pageLengthsInLines) {
        List<String> lines = pages.lines().toList();
        if (pageLengthsInLines.get(0) > lines.size()) return List.of(pages);

        var indexes = generateIndexes(pageLengthsInLines, lines.size());
        var result = new ArrayList<String>();
        for (int i = 1; i < indexes.length; i++) {
            result.add(joinLines(lines.subList(indexes[i - 1], indexes[i])));
        }

        return result;
    }

    private int[] generateIndexes(List<Integer> pageLengthsInRows, int totalLineCount) {
        var zero = IntStream.of(0);
        var pageLengthsAsArray = concat(zero, pageLengthsInRows.stream().mapToInt(x -> x > 0 ? x : throwIllegalArgument(x))).toArray();
        Arrays.parallelPrefix(pageLengthsAsArray, Integer::sum);
        var length = IntStream.of(totalLineCount);
        return concat(Arrays.stream(pageLengthsAsArray).filter(len -> len < totalLineCount), length).toArray();
    }

    private int throwIllegalArgument(int pageLength) {
        throw new IllegalArgumentException("Page lengths must be strictly positive but received length " + pageLength);
    }


    @Override
    public void dischargeTo(PageConsumer consumer) {
        for (int i = 0; i < pages.size(); i++) {
            var page = pages.get(i);
            consumer.consumePage(new SimpleTextPage(page, i));
        }
        consumer.finish();
    }
}
