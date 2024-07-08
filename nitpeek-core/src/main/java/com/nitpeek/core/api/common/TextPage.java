package com.nitpeek.core.api.common;

import java.util.List;

public interface TextPage {

    /**
     * @return the text on this page as a list of lines
     */
    List<String> getLines();

    /**
     * @return the number (ordinal, 0-based) of this page
     */
    int getPageNumber();
}
