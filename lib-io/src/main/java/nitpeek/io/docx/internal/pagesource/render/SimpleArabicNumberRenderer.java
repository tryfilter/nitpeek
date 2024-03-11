package nitpeek.io.docx.internal.pagesource.render;

public final class SimpleArabicNumberRenderer implements NumberRenderer {
    @Override
    public String renderFootnoteNumber(int footnoteNumber) {
        return renderInt(footnoteNumber);
    }

    @Override
    public String renderPageNumber(int pageNumber) {
        return renderInt(pageNumber);
    }

    private String renderInt(int number) {
        return String.valueOf(number);
    }
}
