package nitpeek.core.impl.analyze;

import nitpeek.core.api.common.TextPage;

import java.util.List;
import java.util.Objects;

public record SimpleTextPage(List<String> lines, int number) implements TextPage {
    public SimpleTextPage {
        if (number < 0) throw new IndexOutOfBoundsException(number);
        Objects.requireNonNull(lines);
        lines = List.copyOf(lines);
    }

    public SimpleTextPage(String pageContent, int number) {
        this(pageContent.lines().toList(), number);
    }

    @Override
    public List<String> getLines() {
        return lines;
    }

    @Override
    public int getPageNumber() {
        return number;
    }
}
