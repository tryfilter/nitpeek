package com.nitpeek.core.api.process;

import com.nitpeek.core.api.common.TextPage;

public interface PageConsumer<R> {
    void consumePage(TextPage page);
    R finish();
}