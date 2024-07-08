package com.nitpeek.io.docx.internal.common;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class PartitionerShould {
    private final List<String> elements = List.of("0", "1", "2", "3", "XYZ", "5", "ABC");

    private final Partitioner<List<String>, String> stringPartitioner = new Partitioner<>(elements, Function.identity());

    @Test
    void partitionEntireList() {
        List<String> fullPartition = stringPartitioner.fullPartition();
        assertEquals(elements, fullPartition);
    }

    @Test
    void partitionEntireListFrom() {
        List<String> fullPartition = stringPartitioner.partitionFrom(0);
        assertEquals(elements, fullPartition);
    }

    @Test
    void partitionEntireListTo() {
        List<String> fullPartition = stringPartitioner.partitionTo(elements.size() - 1);
        assertEquals(elements, fullPartition);
    }

    @Test
    void partitionPartialList() {
        int firstIndex = 1;
        int lastIndex = 3;
        List<String> partition = stringPartitioner.partitionBetween(firstIndex, lastIndex);
        assertEquals(elements.subList(firstIndex, lastIndex + 1), partition);
    }

    @Test
    void partitionPartialListFrom() {
        int firstIndex = 2;
        List<String> partition = stringPartitioner.partitionFrom(firstIndex);
        assertEquals(elements.subList(firstIndex, elements.size()), partition);
    }

    @Test
    void partitionPartialListTo() {
        int lastIndex = 5;
        List<String> partition = stringPartitioner.partitionTo(lastIndex);
        assertEquals(elements.subList(0, lastIndex + 1), partition);
    }

    @Test
    void partitionSingleElement() {
        int firstIndex = 4;
        int lastIndex = firstIndex;
        List<String> partition = stringPartitioner.partitionBetween(lastIndex, lastIndex);
        assertEquals(elements.subList(firstIndex, lastIndex + 1), partition);
    }

    private static String concat(List<String> strings) {
        return String.join("", strings);
    }
}