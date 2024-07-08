package com.nitpeek.io.docx.internal.common;

public interface RunRendererFactory {

    /**
     * @param pageIndex the o based index of the page the to-be-rendered runs belong to
     * @param pageCount the total number of pages in the document
     * @return a RunRenderer configured using the provided pageIndex and pageCount
     */
    RunRenderer createRunRenderer(int pageIndex, int pageCount);
}
