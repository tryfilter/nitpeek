package nitpeek.io.docx.internal.common;

public interface Partitioned<T> {
    T fullPartition();

    T partitionBetween(int firstIndex, int lastIndex);

    T partitionFrom(int firstIndex);

    T partitionTo(int lastIndex);
}
