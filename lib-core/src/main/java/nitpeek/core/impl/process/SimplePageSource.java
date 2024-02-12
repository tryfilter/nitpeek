package nitpeek.core.impl.process;

import nitpeek.core.api.common.TextPage;
import nitpeek.core.api.process.PageConsumer;
import nitpeek.core.api.process.PageSource;

import java.util.List;

public final class SimplePageSource implements PageSource {

    private final List<TextPage> pages;

    public SimplePageSource(List<TextPage> pages) {
        this.pages = List.copyOf(pages);
    }

    @Override
    public void dischargeTo(PageConsumer consumer) {
        for (var page : pages) {
            consumer.consumePage(page);
        }
        consumer.finish();
    }
}
