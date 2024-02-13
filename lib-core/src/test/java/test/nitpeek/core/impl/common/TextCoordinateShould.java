package test.nitpeek.core.impl.common;

import nitpeek.core.api.common.TextCoordinate;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class TextCoordinateShould {

    @Test()
    void throwIfConstructedFromNegativePage() {
        assertThrows(IllegalArgumentException.class, () -> new TextCoordinate(-3, 3, 3));
    }

    @Test()
    void throwIfConstructedFromNegativeLine() {
        assertThrows(IllegalArgumentException.class, () -> new TextCoordinate(3, -3, 3));
    }

    @Test()
    void throwIfConstructedFromNegativeChar() {
        assertThrows(IllegalArgumentException.class, () -> new TextCoordinate(3, 3, -3));
    }

    @Test()
    void constructSuccessfullyFromAllPositive() {
        assertDoesNotThrow(() -> new TextCoordinate(3, 3, 3));
    }
}
