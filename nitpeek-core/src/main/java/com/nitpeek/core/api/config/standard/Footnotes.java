package com.nitpeek.core.api.config.standard;

import com.nitpeek.core.api.common.TextSelection;
import com.nitpeek.core.api.config.standard.footnote.Footnote;

import java.util.Map;

public interface Footnotes {

    Map<TextSelection, Footnote> allFootnotes();
    Map<TextSelection, Footnote> footnotesForPage(int pageIndex);
}