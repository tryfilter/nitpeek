package nitpeek.io.docx.internal.pagesource;

public interface NumberRenderer {

    String renderFootnoteNumber(int footnoteNumber);
    String renderPageNumber(int pageNumber);
}
