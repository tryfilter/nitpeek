package nitpeek.core.api.common;

/**
 * Describes a precise location inside a text
 *
 * @param page   the ordinal of the page, 0-based
 * @param row    the ordinal of the row, 0-based
 * @param column the ordinal of the column, 0-based. This refers to a specific character in the row
 */
public record TextCoordinate(int page, int row, int column) {
}
