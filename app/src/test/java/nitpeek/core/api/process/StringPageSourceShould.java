package nitpeek.core.api.process;

import nitpeek.core.api.analyze.SimpleTextPage;
import nitpeek.core.api.analyze.TextPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

        var pageSource = new StringPageSource(concat(PAGE0));
        pageSource.dischargeTo(consumer);

        var expected = List.of(page(PAGE0, 0));
        var actual = consumer.getPages();
        assertEquals(expected, actual);
    }

    @Test
    void reproduceMultiplePagesInOrder() {

        var pageSource = new StringPageSource(List.of(concat(PAGE0), concat(PAGE1), concat(PAGE2)));
        pageSource.dischargeTo(consumer);

        assertPages0and1and2();
    }

    @Test
    void producePagesSplitByRegex() {
        String concatenatedPages = concat(PAGE0) + "separator 00" + concat(PAGE1) + "separator 01" + concat(PAGE2);
        Pattern splitBy = Pattern.compile("separator \\d{2}");

        var pageSource = new StringPageSource(concatenatedPages, splitBy);

        pageSource.dischargeTo(consumer);
        assertPages0and1and2();
    }

    @Test
    void produceSinglePageSplitByRegexWithNoMatch() {
        String concatenatedPages = concat(PAGE0) + "separator 00" + concat(PAGE1) + "separator 01" + concat(PAGE2);
        Pattern splitBy = Pattern.compile("separator \\d{30}");

        var pageSource = new StringPageSource(concatenatedPages, splitBy);

        pageSource.dischargeTo(consumer);
        var expected = List.of(new SimpleTextPage(concatenatedPages, 0));
        var actual = consumer.getPages();

        assertEquals(expected, actual);
    }

    @Test
    void producePagesSplitByLineCountSinglePage() {
        String concatenatedPages = concat(PAGE0) + "\n" + concat(PAGE1) + "\n" + concat(PAGE2);
        int linesPerPage = 100;

        var pageSource = new StringPageSource(concatenatedPages, linesPerPage);

        pageSource.dischargeTo(consumer);
        assertSinglePage(concatenatedPages);
    }

    @Test
    void producePagesSplitByLineCount() {
        String concatenatedPages = concat(PAGE0) + "\n" + concat(PAGE1) + "\n" + concat(PAGE2);
        int linesPerPage = 3;

        var pageSource = new StringPageSource(concatenatedPages, linesPerPage);

        pageSource.dischargeTo(consumer);
        assertPages0and1and2();
    }

    @Test
    void producePagesSplitByLineCountLastPageShorter() {
        String concatenatedPages = concat(PAGE0) + "\n" + concat(PAGE1) + "\n" + concat(PAGE2) + "\n" + concat(PAGE3);
        int linesPerPage = 3;

        var pageSource = new StringPageSource(concatenatedPages, linesPerPage);

        pageSource.dischargeTo(consumer);
        assertPages(List.of(PAGE0, PAGE1, PAGE2, PAGE3));
    }

    @Test
    void producePagesSplitByLineCountLastPageLonger() {
        String concatenatedPages = concat(PAGE0) + "\n" + concat(PAGE1) + "\n" + concat(PAGE2) + "\n" + concat(PAGE4);
        int linesPerPage = 3;

        var pageSource = new StringPageSource(concatenatedPages, linesPerPage);

        pageSource.dischargeTo(consumer);
        assertPages(List.of(PAGE0, PAGE1, PAGE2, PAGE4.subList(0, 3), PAGE4.subList(3, 4)));
    }

    @Test
    void producePagesSplitByEqualLineCounts() {
        String concatenatedPages = concat(PAGE0) + "\n" + concat(PAGE1) + "\n" + concat(PAGE2);
        List<Integer> linesPerPage = List.of(3, 3, 3);

        var pageSource = new StringPageSource(concatenatedPages, linesPerPage);

        pageSource.dischargeTo(consumer);
        assertPages0and1and2();
    }

    @Test
    void producePagesSplitByLineCountsSinglePage() {
        String concatenatedPages = concat(PAGE0) + "\n" + concat(PAGE1) + "\n" + concat(PAGE2);
        List<Integer> linesPerPage = List.of(100, 3, 3);

        var pageSource = new StringPageSource(concatenatedPages, linesPerPage);
        pageSource.dischargeTo(consumer);
        assertSinglePage(concatenatedPages);
    }

    @Test
    void producePagesSplitByLineCountsExactLengths() {
        String concatenatedPages = concat(PAGE2) + "\n" + concat(PAGE3) + "\n" + concat(PAGE1);
        List<Integer> linesPerPage = List.of(3, 2, 3);

        var pageSource = new StringPageSource(concatenatedPages, linesPerPage);
        pageSource.dischargeTo(consumer);
        assertPages(List.of(PAGE2, PAGE3, PAGE1));
    }

    @Test
    void producePagesSplitByLineCountsLeftOverLengths() {
        String concatenatedPages = concat(PAGE2) + "\n" + concat(PAGE3) + "\n" + concat(PAGE1);
        List<Integer> linesPerPage = List.of(3, 2, 3, 5, 1, 10);

        var pageSource = new StringPageSource(concatenatedPages, linesPerPage);
        pageSource.dischargeTo(consumer);
        assertPages(List.of(PAGE2, PAGE3, PAGE1));
    }

    @Test
    void producePagesSplitByLineCountsLeftOverLines() {
        String concatenatedPages = concat(PAGE2) + "\n" + concat(PAGE3) + "\n" + concat(PAGE1);
        List<Integer> linesPerPage = List.of(3, 2);

        var pageSource = new StringPageSource(concatenatedPages, linesPerPage);
        pageSource.dischargeTo(consumer);
        assertPages(List.of(PAGE2, PAGE3, PAGE1));
    }

    @Test
    void producePagesSplitByLineCountsLeftOverLine() {
        String concatenatedPages = concat(PAGE2) + "\n" + concat(PAGE3);
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

    private String concat(List<String> lines) {
        return String.join("\n", lines.toArray(new String[0]));
    }
}
