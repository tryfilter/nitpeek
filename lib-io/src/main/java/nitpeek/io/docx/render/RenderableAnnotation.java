package nitpeek.io.docx.render;

import nitpeek.io.docx.internal.pagesource.DocxSegment;

import java.util.List;

/**
 * Represents an annotation in a final form prepared to be rendered in a DOCX document.
 * Crucially, its extents are on run borders, meaning any manipulations can be applied directly to any of the runs that
 * are part of the annotation.
 *
 * @param message  represents the text & author of the annotation
 * @param segments the segments that make up the selection of text in the document that the annotation pertains to
 */
public record RenderableAnnotation(Message message, List<DocxSegment> segments) {
}
