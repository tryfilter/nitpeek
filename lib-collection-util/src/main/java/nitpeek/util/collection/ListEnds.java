package nitpeek.util.collection;

import java.util.List;

public final class ListEnds<T> {
    private final List<T> list;

    public ListEnds(List<T> list) {
        this.list = List.copyOf(list);
        if (this.list.isEmpty()) throw new IllegalArgumentException("list must be non-empty");
    }

    public T first() {
        return list.getFirst();
    }

    public T last() {
        return list.getLast();
    }

    /**
     * @return an unmodifiable copy
     */
    public List<T> middle() {
        if (list.size() == 1) return List.of();
        return list.subList(1, list.size() - 1);
    }
}
