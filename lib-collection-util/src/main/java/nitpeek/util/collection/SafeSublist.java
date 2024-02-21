package nitpeek.util.collection;

import java.util.List;

import static java.lang.Math.max;
import static java.lang.Math.min;

public final class SafeSublist {

    private SafeSublist() {

    }

    public static <T> List<T> subList(List<T> elements, int firstIndex, int lastIndex) {
        if (firstIndex > lastIndex || lastIndex < 0 || firstIndex >= elements.size()) return List.of();
        int startIndex = max(0, firstIndex);
        int endIndex = min(elements.size(), lastIndex + 1);
        return elements.subList(startIndex, endIndex);
    }

    public static <T> List<T> subListFrom(List<T> elements, int firstIndex) {
        return subList(elements, firstIndex, elements.size() - 1);
    }

    public static <T> List<T> subListTo(List<T> elements, int lastIndex) {
        return subList(elements, 0, lastIndex);
    }
}
