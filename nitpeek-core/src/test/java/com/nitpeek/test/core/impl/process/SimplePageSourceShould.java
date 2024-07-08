package com.nitpeek.test.core.impl.process;

import com.nitpeek.core.api.common.TextPage;
import com.nitpeek.core.api.process.PageConsumer;
import com.nitpeek.core.impl.analyze.SimpleTextPage;
import com.nitpeek.core.impl.process.ListPageConsumer;
import com.nitpeek.core.impl.process.SimplePageSource;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

final class SimplePageSourceShould {

    private static final TextPage p1 = new SimpleTextPage("Page 1 sample content", 0);
    private static final TextPage p2 = new SimpleTextPage("Page 2 sample content", 1);
    private static final TextPage p3 = new SimpleTextPage("Page 3 sample content", 2);

    @Test
    void reproduceMultiplePagesInOrder() {

        var pages = List.of(p1, p2, p3);
        var consumer = new ListPageConsumer();

        var pageSource = new SimplePageSource(pages);
        pageSource.dischargeTo(consumer);

        assertEquals(pages, consumer.getPages());
    }

    @Test
    void finishConsumer() {
        var pages = List.of(p1, p2, p3);
        var consumer = mock(PageConsumer.class);

        var pageSource = new SimplePageSource(pages);
        pageSource.dischargeTo(consumer);

        verify(consumer).consumePage(p1);
        verify(consumer).consumePage(p2);
        verify(consumer).consumePage(p3);
        verify(consumer).finish();
    }
}