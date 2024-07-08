package com.nitpeek.io.docx.internal.pagesource;

import com.nitpeek.io.docx.types.DocxPage;
import com.nitpeek.core.api.config.standard.Footnotes;
import com.nitpeek.core.api.config.standard.FootnotesProvider;
import com.nitpeek.core.api.config.standard.footnote.Footnote;
import com.nitpeek.core.api.config.standard.footnote.FootnoteContent;
import com.nitpeek.core.api.config.standard.footnote.FootnoteReference;
import com.nitpeek.core.api.config.standard.footnote.SimpleFootnotes;
import com.nitpeek.io.docx.types.CompositeRun;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class DocxFootnotesProvider implements FootnotesProvider {
    private final List<? extends DocxPage<? extends CompositeRun>> pages;
    private final FootnoteReferenceExtractor footnoteReferenceExtractor;

    private final FootnoteContentExtractor footnoteContentExtractor;


    public DocxFootnotesProvider(List<? extends DocxPage<? extends CompositeRun>> pages, FootnoteReferenceExtractor footnoteReferenceExtractor, FootnoteContentExtractor footnoteContentExtractor) {
        this.pages = pages;
        this.footnoteReferenceExtractor = footnoteReferenceExtractor;
        this.footnoteContentExtractor = footnoteContentExtractor;
    }

    @Override
    public Footnotes getFootnotes() {

        var footnoteContents = getFootnoteContents();
        var footnoteReferences = getFootnoteReferences();

        return new SimpleFootnotes(assembleFootnotes(footnoteReferences, footnoteContents));
    }

    private Set<Footnote> assembleFootnotes(Map<Integer, FootnoteReference> footnoteReferences, Map<Integer, FootnoteContent> footnoteContents) {
        var result = new HashSet<Footnote>();
        for (var entry : footnoteReferences.entrySet()) {
            int ordinal = entry.getKey();
            FootnoteReference reference = entry.getValue();
            result.add(new Footnote(reference, footnoteContents.get(ordinal)));
        }
        return result;
    }

    private Map<Integer, FootnoteReference> getFootnoteReferences() {
        return mergeToMap(this::extractFootnoteReferences);
    }

    private Map<Integer, FootnoteContent> getFootnoteContents() {
        return mergeToMap(this::extractFootnoteContents);
    }

    private Map<Integer, FootnoteReference> extractFootnoteReferences(int pageIndex, DocxPage<? extends CompositeRun> page) {
        return footnoteReferenceExtractor.extractFootnoteReferences(page, pageIndex).stream()
                .collect(Collectors.toMap(FootnoteReference::ordinal, Function.identity()));
    }

    private Map<Integer, FootnoteContent> extractFootnoteContents(int pageIndex, DocxPage<? extends CompositeRun> page) {
        return footnoteContentExtractor.extractFootnoteContents(page, pageIndex);
    }

    private <V> Map<Integer, V> mergeToMap(BiFunction<Integer, DocxPage<? extends CompositeRun>, Map<Integer, V>> itemToMap) {
        var result = new HashMap<Integer, V>();
        for (int p = 0; p < pages.size(); p++) {
            var page = pages.get(p);
            result.putAll(itemToMap.apply(p, page));
        }
        return result;
    }
}