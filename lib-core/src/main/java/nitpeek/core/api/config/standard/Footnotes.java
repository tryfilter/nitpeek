package nitpeek.core.api.config.standard;

import nitpeek.core.api.common.TextSelection;
import nitpeek.core.api.config.standard.footnote.Footnote;

import java.util.Map;

public interface Footnotes {

    Map<TextSelection, Footnote> allFootnotes();
    Map<TextSelection, Footnote> footnotesForPage(int pageIndex);
}