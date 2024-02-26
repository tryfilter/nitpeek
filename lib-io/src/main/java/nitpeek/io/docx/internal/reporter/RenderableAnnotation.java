package nitpeek.io.docx.internal.reporter;

/**
 * Represents an annotation that includes all information necessary to display it in a DOCX document.<br>
 * Note that it contains references to underlying objects of a DOCX document, and displaying the annotation would
 * typically involve manipulating these underlying objects in some way.
 * @param message represents the text & author of the annotation
 * @param textSelection defines the selection of text in the document that the annotation pertains to
 */
record RenderableAnnotation(Message message, DocxTextSelection textSelection) {
}
