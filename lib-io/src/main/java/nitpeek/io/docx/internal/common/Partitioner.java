package nitpeek.io.docx.internal.common;

import nitpeek.io.docx.internal.pagesource.Partitioned;

import java.util.List;
import java.util.function.Function;

final class Partitioner<P, E> implements Partitioned<P> {

    private final List<E> elements;
    private final Function<List<E>, P> partitionFactory;

    Partitioner(List<E> elements, Function<List<E>, P> partitionFactory) {
        this.elements = List.copyOf(elements);
        this.partitionFactory = partitionFactory;
    }

    @Override
    public P partitionFrom(int firstIndex) {
        return partitionBetween(firstIndex, elements.size() - 1);
    }

    @Override
    public P partitionTo(int lastIndex) {
        return partitionBetween(0, lastIndex);
    }

    @Override
    public P fullPartition() {
        return partitionFactory.apply(elements);
    }

    @Override
    public P partitionBetween(int firstIndex, int lastIndex) {
        return partitionFactory.apply(elements.subList(firstIndex, lastIndex + 1));
    }
}
