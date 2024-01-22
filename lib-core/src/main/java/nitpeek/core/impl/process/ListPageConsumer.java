package nitpeek.core.impl.process;

import nitpeek.core.api.process.PageConsumer;
import nitpeek.core.api.process.PageSource;
import nitpeek.core.api.common.TextPage;

import java.util.ArrayList;
import java.util.List;

public final class ListPageConsumer implements PageConsumer {

    private volatile boolean finished = false;

    private final List<TextPage> consumedPages = new ArrayList<>();

    public ListPageConsumer() {}

    /**
     * Constructs an already finished consumer containing all the pages of {@code source}
     * @param source the PageSource to consume; the source is already consumed when this constructor exits
     */
    public ListPageConsumer(PageSource source) {
        source.dischargeTo(this);
    }
    @Override
    public void consumePage(TextPage page) {
        if (finished) throw new IllegalStateException("This ListPageConsumer has already finished, consuming new pages is not supported.");

        consumedPages.add(page);
    }

    @Override
    public void finish() {
        finished = true;
    }

    /**
     * @return an unmodifiable snapshot
     */
    public List<TextPage> getPages() {
        return List.copyOf(consumedPages);
    }
}
