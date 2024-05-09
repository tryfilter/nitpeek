package nitpeek.core.api.process;

import nitpeek.core.api.common.TextPage;

public interface PageConsumer<R> {
    void consumePage(TextPage page);
    R finish();
}