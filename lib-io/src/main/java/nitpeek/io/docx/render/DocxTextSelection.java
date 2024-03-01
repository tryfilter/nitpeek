package nitpeek.io.docx.render;

import nitpeek.io.docx.internal.pagesource.DocxSegment;

import java.util.List;

/**
 * Represents a segment of contiguous text, with maximum precision, i.e. the extents being defined at the "character"
 * granularity level.<br>
 * <br>
 * Note that the values of {@code indexOfFirstCharacter} and {@code indexOfLastCharacter} of a particular
 * {@code DocxTextSelection} instance are intrinsically tied to a particular implementation of a
 * {@code nitpeek.io.docx.internal.pagesource.ParagraphRenderer}.
 *
 * @param segments              defines the segments containing the paragraphs that constitute this selection
 * @param indexOfFirstCharacter defines within the first run of the first paragraph in this selection, the index of
 *                              the first character that is part of this selection
 * @param indexOfLastCharacter  defines within the last run of the last paragraph in this selection, the index of
 *                              the last character that is part of this selection
 */
public record DocxTextSelection(List<DocxSegment> segments, int indexOfFirstCharacter, int indexOfLastCharacter) {

    public DocxTextSelection {
        segments = List.copyOf(segments);
    }
}
