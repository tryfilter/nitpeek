package nitpeek.io.docx.internal.common;

import nitpeek.io.docx.types.CompositeRun;
import nitpeek.io.docx.types.DocxSegment;

/**
 * Represents a segment of text, with maximum precision, i.e. the extents being defined at the "character"
 * granularity level.<br>
 * <br>
 * Note that the values of {@code indexOfFirstCharacter} and {@code indexOfLastCharacter} of a particular
 * {@code DocxTextSelection} instance are intrinsically tied to a particular implementation of a
 * {@code nitpeek.io.docx.internal.pagesource.render.ParagraphRenderer}.
 */
public interface DocxTextSelection<C extends CompositeRun> {

    /**
     * @return the segment containing the paragraphs that constitute this selection
     */
    DocxSegment<C> segment();

    /**
     * @return within the first run of the first paragraph in this selection, the index of the first character that is
     * part of this selection
     */
    int indexOfFirstCharacter();

    /**
     * @return within the last run of the last paragraph in this selection, the index of the last character that is
     * part of this selection
     */
    int indexOfLastCharacter();

}
