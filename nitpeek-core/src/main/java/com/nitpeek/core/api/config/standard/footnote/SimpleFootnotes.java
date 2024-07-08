package com.nitpeek.core.api.config.standard.footnote;

import com.nitpeek.core.api.common.TextSelection;
import com.nitpeek.core.api.config.standard.Footnotes;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class SimpleFootnotes implements Footnotes {

    private final Map<TextSelection, Footnote> allFootnotes;

    public SimpleFootnotes(Set<Footnote> allFootnotes) {
        this.allFootnotes = allFootnotes.stream()
                .collect(Collectors.toUnmodifiableMap(footnote -> footnote.reference().selection(), Function.identity()));
    }

    @Override
    public Map<TextSelection, Footnote> allFootnotes() {
        return Map.copyOf(allFootnotes);
    }

    @Override
    public Map<TextSelection, Footnote> footnotesForPage(int pageIndex) {
        return allFootnotes().entrySet().stream()
                .filter(entry -> entry.getKey().fromInclusive().page() == pageIndex)
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}