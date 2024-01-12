package nitpeek.core.api.common;

/**
 * Describes a precise location inside a text
 *
 * @param page      the ordinal of the page, 0-based, not negative
 * @param line      the ordinal of the line, 0-based, not negative. This refers to a specific line in the page
 * @param character the ordinal of the character, 0-based, not negative. This refers to a specific character in the line
 */
public record TextCoordinate(int page, int line, int character) {
    public TextCoordinate {
        if (page < 0) throw new IllegalArgumentException("page must be >= 0, was " + page);
        if (line < 0) throw new IllegalArgumentException("line must be >= 0, was " + line);
        if (character < 0) throw new IllegalArgumentException("character must be >= 0, was " + character);
    }

    public TextSelection extendToSelection(int totalCharacters) {
        return extendToSelection(1, totalCharacters);
    }

    public TextSelection extendToSelection(int totalLines, int totalCharacters) {
        return extendToSelection(1, totalLines, totalCharacters);
    }

    public TextSelection extendToSelection(int totalPages, int totalLines, int totalCharacters) {
        return new TextSelection(this, new TextCoordinate(page() + totalPages - 1, line() + totalLines - 1, character() + totalCharacters - 1));
    }
}
