package nitpeek.core.impl.common;

import nitpeek.core.api.common.TextCoordinate;
import nitpeek.core.api.common.TextSelection;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class TextSelectionShould {

    private static final TextCoordinate BASE = new TextCoordinate(2, 2, 2);
    private static final TextCoordinate ILLEGAL_SMALLER_PAGE = new TextCoordinate(1, 2, 2);
    private static final TextCoordinate ILLEGAL_SMALLER_LINE = new TextCoordinate(2, 1, 2);
    private static final TextCoordinate ILLEGAL_SMALLER_CHAR = new TextCoordinate(2, 2, 1);
    
    private static final TextCoordinate LEGAL_SMALLER_LINE = new TextCoordinate(3, 1, 2);
    private static final TextCoordinate LEGAL_SMALLER_LINE_AND_CHAR = new TextCoordinate(3, 1, 1);
    private static final TextCoordinate LEGAL_SMALLER_CHAR = new TextCoordinate(2, 3, 1);



    @ParameterizedTest
    @MethodSource("getIllegalEndCoordinates")
    void throwWhenConstructedFromIllegalRange(TextCoordinate endCoordinate) {
        assertThrows(IllegalArgumentException.class, () -> new TextSelection(BASE, endCoordinate));
    }

    @ParameterizedTest
    @MethodSource("getLegalEndCoordinates")
    void notThrowWhenConstructedFromLegalRange(TextCoordinate endCoordinate) {
        assertDoesNotThrow(() -> new TextSelection(BASE, endCoordinate));
    }

    @Test
    void notThrowWhenConstructedAsEmpty() {
        assertDoesNotThrow(() -> new TextSelection(BASE, BASE));
    }

    private static Stream<TextCoordinate> getIllegalEndCoordinates() {
        return Stream.of(ILLEGAL_SMALLER_PAGE, ILLEGAL_SMALLER_LINE, ILLEGAL_SMALLER_CHAR);
    }

    private static Stream<TextCoordinate> getLegalEndCoordinates() {
        return Stream.of(LEGAL_SMALLER_LINE, LEGAL_SMALLER_CHAR, LEGAL_SMALLER_LINE_AND_CHAR);
    }
}
