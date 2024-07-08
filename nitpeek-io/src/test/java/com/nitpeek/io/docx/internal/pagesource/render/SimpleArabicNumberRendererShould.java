package com.nitpeek.io.docx.internal.pagesource.render;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class SimpleArabicNumberRendererShould {

    private final NumberRenderer numberRenderer = new SimpleArabicNumberRenderer();

    @Test
    void renderFootnote() {
        int footnote = 42;
        assertEquals("42", numberRenderer.renderFootnoteNumber(footnote));
    }

    @Test
    void renderPageNumber() {
        int pageNumber = 42;
        assertEquals("42", numberRenderer.renderPageNumber(pageNumber));
    }


}