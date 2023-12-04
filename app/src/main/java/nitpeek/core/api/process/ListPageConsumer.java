package nitpeek.core.api.process;

import nitpeek.core.api.analyze.TextPage;

import java.util.ArrayList;
import java.util.List;

public final class ListPageConsumer implements PageConsumer {

    private volatile boolean finished = false;

    private final List<TextPage> consumedPages = new ArrayList<>();
    @Override
    public void consumePage(TextPage page) {
        if (finished) throw new IllegalStateException("This ListPageConsumer has already finished, consuming new pages is not supported.");

        consumedPages.add(page);
    }

    @Override
    public void finish() {
        finished = true;
    }

    public List<TextPage> getPages() {
        return List.copyOf(consumedPages);
    }
}
