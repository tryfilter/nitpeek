package nitpeek.core.api.analyze;

public interface TextPage {

    /**
     * @return the text on this page
     */
    String getText();

    /**
     * @return the number (ordinal, 0-based) associated with this page
     */
    int getPageNumber();
}
