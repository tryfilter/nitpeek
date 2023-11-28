package nitpeek.core.api.common;

/**
 * Describes a precise location inside a text
 *
 * @param page      the ordinal of the page, 0-based
 * @param line      the ordinal of the line, 0-based. This refers to a specific line in the page
 * @param character the ordinal of the character, 0-based. This refers to a specific character in the line
 */
public record TextCoordinate(int page, int line, int character) {
}
