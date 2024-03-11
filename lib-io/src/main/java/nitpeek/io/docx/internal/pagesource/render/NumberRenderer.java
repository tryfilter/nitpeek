package nitpeek.io.docx.internal.pagesource.render;

public interface NumberRenderer {

    String renderFootnoteNumber(int footnoteNumber);
    String renderPageNumber(int pageNumber);
}
