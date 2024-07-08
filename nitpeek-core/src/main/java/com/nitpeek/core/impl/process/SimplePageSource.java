package com.nitpeek.core.impl.process;

import com.nitpeek.core.api.common.TextPage;
import com.nitpeek.core.api.process.PageConsumer;
import com.nitpeek.core.api.process.PageSource;

import java.util.List;

public final class SimplePageSource implements PageSource {

    private final List<TextPage> pages;

    public SimplePageSource(List<TextPage> pages) {
        this.pages = List.copyOf(pages);
    }

    @Override
    public <R> R dischargeTo(PageConsumer<R> consumer) {
        for (var page : pages) {
            consumer.consumePage(page);
        }
        return consumer.finish();
    }
}