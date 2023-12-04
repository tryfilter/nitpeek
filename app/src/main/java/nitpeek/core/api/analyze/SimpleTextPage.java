package nitpeek.core.api.analyze;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public record SimpleTextPage(List<String> lines, int number) implements TextPage {
    public SimpleTextPage {
        if (number < 0) throw new IndexOutOfBoundsException(number);
        Objects.requireNonNull(lines);
        lines = new ArrayList<>(lines);
    }

    public SimpleTextPage(String pageContent, int number) {
        this(pageContent.lines().toList(), number);
    }

    @Override
    public List<String> getLines() {
        return Collections.unmodifiableList(lines);
    }

    @Override
    public int getPageNumber() {
        return number;
    }
}
