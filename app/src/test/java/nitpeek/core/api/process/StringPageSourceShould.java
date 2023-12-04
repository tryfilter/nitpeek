package nitpeek.core.api.process;

import nitpeek.core.api.analyze.SimpleTextPage;
import nitpeek.core.api.analyze.TextPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class StringPageSourceShould {

    private static final List<String> PAGE0 = List.of(
            "Simple Heading Page 0",
            "Simple Subheading Page 0",
            "Simple Paragraph Page 0"
    );

    private static final List<String> PAGE1 = List.of(
            "Simple Heading Page 1",
            "Simple Subheading Page 1",
            "Simple Paragraph Page 1"
    );

    private static final List<String> PAGE2 = List.of(
            "Simple Heading Page 2",
            "Simple Subheading Page 2",
            "Simple Paragraph Page 2"
    );

    private static final List<String> PAGE3 = List.of(
            "Simple Heading Page 3",
            "Simple Text Page 3"
    );

    private static final List<String> PAGE4 = List.of(
            "Simple Heading Page 4",
            "Simple Subheading Page 4",
            "Simple Paragraph Page 4",
            "Another Simple Paragraph Page 4"
    );

    private ListPageConsumer consumer;

    @BeforeEach
    void setup() {
        consumer = new ListPageConsumer();
    }

    @Test
    void reproduceSinglePage() {

        var pageSource = new StringPageSource(join(PAGE0));
        pageSource.dischargeTo(consumer);

        var expected = List.of(page(PAGE0, 0));
        var actual = consumer.getPages();
        assertEquals(expected, actual);
    }

    @Test
    void reproduceMultiplePagesInOrder() {

        var pageSource = new StringPageSource(List.of(join(PAGE0), join(PAGE1), join(PAGE2)));
        pageSource.dischargeTo(consumer);

        assertPages0and1and2();
    }

    @Test
    void producePagesSplitByRegex() {
        String concatenatedPages = join(PAGE0) + "separator 00" + join(PAGE1) + "separator 01" + join(PAGE2);
        Pattern splitBy = Pattern.compile("separator \\d{2}");

        var pageSource = new StringPageSource(concatenatedPages, splitBy);

        pageSource.dischargeTo(consumer);
        assertPages0and1and2();
    }

    @Test
    void produceSinglePageSplitByRegexWithNoMatch() {
        String concatenatedPages = join(PAGE0) + "separator 00" + join(PAGE1) + "separator 01" + join(PAGE2);
        Pattern splitBy = Pattern.compile("separator \\d{30}");

        var pageSource = new StringPageSource(concatenatedPages, splitBy);

        pageSource.dischargeTo(consumer);
        var expected = List.of(new SimpleTextPage(concatenatedPages, 0));
        var actual = consumer.getPages();

        assertEquals(expected, actual);
    }

    @Test
    void producePagesSplitByLineCountSinglePage() {
        String concatenatedPages = joinByNewlines(List.of(PAGE0,PAGE1,PAGE2));
        int linesPerPage = 100;

        var pageSource = new StringPageSource(concatenatedPages, linesPerPage);
        pageSource.dischargeTo(consumer);

        assertSinglePage(concatenatedPages);
    }

    @Test
    void throwsWhenLinesPerPageLessThan1() {
        String concatenatedPages = joinByNewlines(List.of(PAGE0,PAGE1,PAGE2));
        int linesPerPage = 0;

        assertThrows(IllegalArgumentException.class, () -> new StringPageSource(concatenatedPages, linesPerPage));
    }

    @Test
    void producesSingleEmptyPageWhenPagesIsEmpty() {
        String emptyPage = "";
        int linesPerPage = 100;

        var pageSource = new StringPageSource(emptyPage, linesPerPage);
        pageSource.dischargeTo(consumer);

        assertSinglePage(emptyPage);
    }

    @Test
    void producePagesSplitByLineCount() {
        String concatenatedPages = joinByNewlines(List.of(PAGE0,PAGE1,PAGE2));
        int linesPerPage = 3;

        var pageSource = new StringPageSource(concatenatedPages, linesPerPage);
        pageSource.dischargeTo(consumer);

        assertPages0and1and2();
    }

    @Test
    void producePagesSplitByLineCountLastPageShorter() {
        String concatenatedPages = joinByNewlines(List.of(PAGE0,PAGE1,PAGE2,PAGE3));
        int linesPerPage = 3;

        var pageSource = new StringPageSource(concatenatedPages, linesPerPage);
        pageSource.dischargeTo(consumer);

        assertPages(List.of(PAGE0, PAGE1, PAGE2, PAGE3));
    }

    @Test
    void producePagesSplitByLineCountLastPageLonger() {
        String concatenatedPages = joinByNewlines(List.of(PAGE0,PAGE1,PAGE2,PAGE4));
        int linesPerPage = 3;

        var pageSource = new StringPageSource(concatenatedPages, linesPerPage);
        pageSource.dischargeTo(consumer);

        assertPages(List.of(PAGE0, PAGE1, PAGE2, PAGE4.subList(0, 3), PAGE4.subList(3, 4)));
    }

    @Test
    void producePagesSplitByEqualLineCounts() {
        String concatenatedPages = joinByNewlines(List.of(PAGE0, PAGE1, PAGE2));
        List<Integer> linesPerPage = List.of(3, 3, 3);

        var pageSource = new StringPageSource(concatenatedPages, linesPerPage);
        pageSource.dischargeTo(consumer);

        assertPages0and1and2();
    }

    @Test
    void throwsWhenLengthsEmpty() {
        String concatenatedPages = joinByNewlines(List.of(PAGE0, PAGE1, PAGE2));
        List<Integer> linesPerPage = List.of();

        assertThrows(IllegalArgumentException.class, () -> new StringPageSource(concatenatedPages, linesPerPage));
    }

    @Test
    void throwsWhenAnyLengthLessThan1() {
        String concatenatedPages = joinByNewlines(List.of(PAGE0, PAGE1, PAGE2));
        List<Integer> linesPerPage = List.of(3, 3, 0);

        assertThrows(IllegalArgumentException.class, () -> new StringPageSource(concatenatedPages, linesPerPage));
    }

    @Test
    void producePagesSplitByLineCountsSinglePage() {
        String concatenatedPages = joinByNewlines(List.of(PAGE0, PAGE1, PAGE2));
        List<Integer> linesPerPage = List.of(100, 3, 3);

        var pageSource = new StringPageSource(concatenatedPages, linesPerPage);
        pageSource.dischargeTo(consumer);

        assertSinglePage(concatenatedPages);
    }

    @Test
    void producePagesSplitByLineCountsExactLengths() {
        String concatenatedPages = joinByNewlines(List.of(PAGE2, PAGE3, PAGE1));
        List<Integer> linesPerPage = List.of(3, 2, 3);

        var pageSource = new StringPageSource(concatenatedPages, linesPerPage);
        pageSource.dischargeTo(consumer);

        assertPages(List.of(PAGE2, PAGE3, PAGE1));
    }

    @Test
    void producePagesSplitByLineCountsLeftOverLengths() {
        String concatenatedPages = joinByNewlines(List.of(PAGE2, PAGE3, PAGE1));
        List<Integer> linesPerPage = List.of(3, 2, 3, 5, 1, 10);

        var pageSource = new StringPageSource(concatenatedPages, linesPerPage);
        pageSource.dischargeTo(consumer);

        assertPages(List.of(PAGE2, PAGE3, PAGE1));
    }

    @Test
    void producePagesSplitByLineCountsLeftOverLengthsPart() {
        String concatenatedPages = joinByNewlines(List.of(PAGE2, PAGE3, PAGE1));
        List<Integer> linesPerPage = List.of(3, 2, 2, 5, 1, 10);

        var pageSource = new StringPageSource(concatenatedPages, linesPerPage);
        pageSource.dischargeTo(consumer);

        assertPages(List.of(PAGE2, PAGE3, PAGE1.subList(0, 2), PAGE1.subList(2, 3)));
    }

    @Test
    void producePagesSplitByLineCountsLeftOverLines() {
        String concatenatedPages = joinByNewlines(List.of(PAGE2, PAGE3, PAGE1));
        List<Integer> linesPerPage = List.of(3, 2);

        var pageSource = new StringPageSource(concatenatedPages, linesPerPage);
        pageSource.dischargeTo(consumer);

        assertPages(List.of(PAGE2, PAGE3, PAGE1));
    }

    @Test
    void producePagesSplitByLineCountsLeftOverLine() {
        String concatenatedPages = joinByNewlines(List.of(PAGE2, PAGE3));
        List<Integer> linesPerPage = List.of(3, 1);

        var pageSource = new StringPageSource(concatenatedPages, linesPerPage);
        pageSource.dischargeTo(consumer);

        assertPages(List.of(PAGE2, PAGE3.subList(0, 1), PAGE3.subList(1, 2)));
    }

    private void assertSinglePage(String pageContent) {
        var expected = List.of(new SimpleTextPage(pageContent, 0));
        var actual = consumer.getPages();
        assertEquals(expected, actual);
    }

    private TextPage page(List<String> lines, int pageNumber) {
        return new SimpleTextPage(lines, pageNumber);
    }

    private void assertPages0and1and2() {
        assertPages(List.of(PAGE0, PAGE1, PAGE2));
    }

    private void assertPages(List<List<String>> pages) {
        var expected = IntStream.range(0, pages.size()).mapToObj(i -> page(pages.get(i), i)).toList();
        var actual = consumer.getPages();
        assertEquals(expected, actual);
    }

    private String joinByNewlines(List<List<String>> pages) {
        return String.join("\n", pages.stream().map(this::join).toList());
    }

    private String join(List<String> lines) {
        return String.join("\n", lines.toArray(new String[0]));
    }
}
