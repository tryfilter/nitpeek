package nitpeek.io.docx.internal.reporter;

import nitpeek.io.docx.types.CompositeRun;
import nitpeek.io.docx.internal.common.DocxTextSelection;
import nitpeek.io.docx.types.Message;
import nitpeek.io.docx.render.RenderableAnnotation;

/**
 * Represents an annotation that includes all information necessary to display it in a DOCX document.<br>
 * Note that it contains references to underlying objects of a DOCX document, and displaying the annotation would
 * typically involve manipulating these underlying objects in some way.<br>
 * A {@code DocxAnnotation} may cross run boundaries, making it generally impractical for direct rendering.
 * For actually rendering an annotation in a DOCX document, a {@link RenderableAnnotation} can be used.
 *
 * @param message       represents the text & author of the annotation
 * @param textSelection defines the selection of text in the document that the annotation pertains to
 */
public record DocxAnnotation<C extends CompositeRun>(Message message, DocxTextSelection<C> textSelection) {
}
