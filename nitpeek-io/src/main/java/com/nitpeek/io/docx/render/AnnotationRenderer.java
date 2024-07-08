package com.nitpeek.io.docx.render;


public interface AnnotationRenderer {

    /**
     * Modifies an underlying DOCX document to incorporate the {@code annotation}
     * @param annotation the annotation to display
     */
    void renderAnnotation(RenderableAnnotation annotation);
}
