package com.nitpeek.util.collection;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class ListEndsShould {

    @Test
    void throwWhenConstructedFromEmptyList() {
        var emptyList = List.of();
        assertThrows(IllegalArgumentException.class, () -> new ListEnds<>(emptyList));
    }

    @Test
    void returnSingleElementAsBothFirstAndLast() {
        String singleElement = "some element";
        var list = List.of(singleElement);
        var listEnds = new ListEnds<>(list);
        assertEquals(singleElement, listEnds.first());
        assertEquals(singleElement, listEnds.last());
    }

    @Test
    void returnEmptyMiddleForSingleElementList() {
        String singleElement = "some element";
        var list = List.of(singleElement);
        var listEnds = new ListEnds<>(list);
        assertEquals(List.of(), listEnds.middle());
    }

    @Test
    void returnFirstAndLastElements() {
        String first = "first element";
        String second = "second element";
        var list = List.of(first, second);
        var listEnds = new ListEnds<>(list);
        assertEquals(first, listEnds.first());
        assertEquals(second, listEnds.last());
    }

    @Test
    void returnEmptyMiddleForTwoElementList() {
        String first = "first element";
        String second = "second element";
        var list = List.of(first, second);
        var listEnds = new ListEnds<>(list);
        assertEquals(List.of(), listEnds.middle());
    }

    @Test
    void returnFirstAndLastElementsWhenNotAdjacent() {
        String first = "first element";
        String second = "second element";
        String third = "third element";
        var list = List.of(first, second, third);
        var listEnds = new ListEnds<>(list);
        assertEquals(first, listEnds.first());
        assertEquals(third, listEnds.last());
    }

    @Test
    void returnMiddleElementForTwoElementList() {
        String first = "first element";
        String second = "second element";
        String third = "third element";
        var list = List.of(first, second, third);
        var listEnds = new ListEnds<>(list);
        assertEquals(List.of(second), listEnds.middle());
    }

    @Test
    void returnMiddleForMultipleElementList() {
        String first = "first element";
        String second = "second element";
        String third = "third element";
        String fourth = "fourth element";
        String fifth = "fifth element";
        var list = List.of(first, second, third, fourth, fifth);
        var listEnds = new ListEnds<>(list);
        assertEquals(List.of(second, third, fourth), listEnds.middle());
    }
}