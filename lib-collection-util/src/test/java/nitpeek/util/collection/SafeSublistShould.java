package nitpeek.util.collection;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class SafeSublistShould {

    private static final List<Integer> sampleList = List.of(1, 2, 3);

    @ParameterizedTest
    @CsvSource({"0,0", "0,1", "1,0", "-2,2"})
    void returnEmptySublistWhenInputListEmpty(int firstIndex, int lastIndex) {
        var sublist = SafeSublist.subList(List.of(), firstIndex, lastIndex);
        assertTrue(sublist.isEmpty());
    }

    @Test
    void returnEmptySublistWhenLastIndexGTFirstIndex() {
        var sublist = SafeSublist.subList(sampleList, 2, 1);
        assertTrue(sublist.isEmpty());
    }

    @Test
    void returnEmptySublistWhenFirstIndexEqualListSize() {
        var sublist = SafeSublist.subList(sampleList, 3, 3);
        assertTrue(sublist.isEmpty());
    }

    @Test
    void returnEmptySublistWhenLastIndexNegative() {
        var sublist = SafeSublist.subList(sampleList, 3, -2);
        assertTrue(sublist.isEmpty());
    }

    @Test
    void returnSingleElementListWithEqualExistingIndexes() {
        var list = List.of("a", "b", "c");
        var sublist = SafeSublist.subList(list, 2, 2);
        assertEquals(List.of("c"), sublist);
    }

    @Test
    void returnEntireListWithRegularEnds() {
        var sublist = SafeSublist.subList(sampleList, 0, 2);
        assertEquals(sampleList, sublist);
    }

    @Test
    void returnEntireListWithOverextendedEnds() {
        var sublist = SafeSublist.subList(sampleList, -1, 3);
        assertEquals(sampleList, sublist);
    }

    @Test
    void returnPrefixListWithRegularEnd() {
        var list = List.of("a", "b", "c");
        var sublist = SafeSublist.subListTo(list, 1);
        assertEquals(List.of("a", "b"), sublist);
    }

    @Test
    void returnPrefixEntireListWithOverextendedEnd() {
        var list = List.of("a", "b", "c");
        var sublist = SafeSublist.subListTo(list, 5);
        assertEquals(list, sublist);
    }

    @Test
    void returnPrefixEmptyListWithUnderExtendedEnd() {
        var list = List.of("a", "b", "c");
        var sublist = SafeSublist.subListTo(list, -1);
        assertEquals(List.of(), sublist);
    }

    @Test
    void returnPostfixListWithRegularEnd() {
        var list = List.of("a", "b", "c");
        var sublist = SafeSublist.subListFrom(list, 1);
        assertEquals(List.of("b", "c"), sublist);
    }

    @Test
    void returnPostfixEmptyListWithOverextendedEnd() {
        var list = List.of("a", "b", "c");
        var sublist = SafeSublist.subListFrom(list, 5);
        assertEquals(List.of(), sublist);
    }

    @Test
    void returnPostfixEntireListWithUnderExtendedEnd() {
        var list = List.of("a", "b", "c");
        var sublist = SafeSublist.subListFrom(list, -1);
        assertEquals(list, sublist);
    }

}