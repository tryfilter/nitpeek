package nitpeek.io.docx.types;

import nitpeek.io.docx.types.CompositeRun;

/**
 * Represents a composite run that may be split.<br>
 * Note that the precise semantics of a split are not specified.
 * Implementations may perform no splitting operation at all.
 */
public interface SplittableRun extends CompositeRun {
    CompositeRun splitBetween(int firstIncludedCharacter, int lastIncludedCharacter);

    CompositeRun splitFrom(int firstIncludedCharacter);

    CompositeRun splitTo(int lastIncludedCharacter);
}